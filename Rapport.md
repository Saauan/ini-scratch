# Rapport

## Préambule : remarque sur le fonctionnement d'INI

### timer.ini

Dans le fichier ini/examples/processes/timer.ini, le programme ne va jamais s'arrêter, car même si les
deux event sont stoppés, il va sans cesse revenir sur la même règle et ne jamais en sortir.

Solution : Mettre un `return` a la fin de la règle `i>10`

## Explication de quelques points du language

### IniMain

Le langage, est présent dans le package `ini-language`. Mais il ne peut pas être lancé seul. Il a besoin d'un `launcher`, un autre package qui va s'occuper de le lancer.

C'est le rôle de `IniMain`. Il va parser les options et les arguments du programme, créer une `org.graalvm.polyglot.Source` (à ne pas confondre avec `com.oracle.truffle.api.source.Source` utilisée dans `IniLanguage`. Elles sont concrètement les mêmes, mais sont utilisées dans des contextes différents) qui contient toutes les informations sur la source du programme à executer par INI (ici un fichier).

Et ensuite, il ne va pas appeler directement IniLanguage, mais va créer un `org.graalvm.polyglot.Context` avec l'`ID` de INI ("INI"), la sortie, l'entrée, et les options.

Et il va simplement appeler `context.eval(source)`, et c'est le contexte qui va aller chercher le langage correspondant à l'ID, et qui va évaluer la source, dans INI et renvoyer le résultat.
(ça peut être pratique, pour pouvoir utiliser des programmes qui font appels à plusieurs langages, mais ici je n'ai pas trouvé de moyen plus simple pour que le langage soit lancé avec le compilateur de GraalVM)

### IniLanguage

`IniLanguage` est la classe qui va lancer le parsing d'une source et renvoyer les noeuds à executer.

Cette classe possède notamment un ID, qui va lui permettre d'être reconnu par GraalVM. Ainsi que des méthodes pour créer ou get le contexte (IniContexte).

La méthode principale de IniLanguage est `parse(ParsingRequest request)`. C'est cette méthode qui est appelée lorsque `IniMain` execute `context.eval(source)`. Elle va lancer un `IniParser`, et parser le code.  
Une fois le code parsé, on récupère les `topLevelNodes` et on les wrap dans une fonction qui est renvoyé à l'appelant.

L'appelant (géré par Graal), va ensuite executer cette fonction.

### Tests

Les tests sont assez intuitifs. Ils sont inspirés des tests de SimpleLanguage. Les fichiers de code qui permettent les tests sont dans `language/src/test/java`.

Mais les ressources de tests, sont dans `language/tests`. Ce sont des fichiers INI ainsi que des fichiers output. Le TestRunner va simpement comparer l'output attendu avec le véritable output du programme INI.

### Remarques pour écrire un programme en INI

Lorsque l'on termine le programme, en renvoyant une valeur, graal va vérifier cette valeur, afin d'éventuellement l'utiliser pour un autre script. Sauf que cette valeur doit implémenter la `Interop Library`. En gros, être une valeur que l'on peut passer entre des langages.

Les `Integer` et la plupart des types de Java sont déjà par défaut dans la `Interop Library`. Donc, si on retourne un entier en INI, tout va bien.
En revanche, les fonctions INI, les listes INI ne sont pas dans cette library. Car je n'ai pas prit le temps d'implémenter l'interface pour elle.

Donc, si on renvoie une fonction (avec une définition de fonction), ou une liste, ou tout autre valeur illégale (pas dans la library), le programme nous affichera un beau message d'erreur nous expliquant cela.

#### Comment contourner le problème ?

Si vous avez un doute sur ce que renvoie votre programme, finissez le par un `return 0`, c'est simple et efficace.

### IniList

Les listes en INI sont implémentées grâce à IniList, qui modélise le comportement d'une liste. (Pour l'instant, il sert de wrapper a une ArrayList).

Pour l'instant, il n'y a pas de builtin pour rajouter un élément à la fin d'une liste (append).
La seule manière disponible actuellement pour rajouter un élément à la fin d'une liste, c'est de le faire à la main.

```
myList = [1,2] // On déclare une liste de deux éléments
myList[2] = 3 // On rajoute un troisième élément à la liste
myList == [1,2,3] // true
```

En revanche, on ne peut pas encore supprimer un élément.

De même, il n'y a pas de type check sur les élements de la liste, et une
même liste peut contenir des éléments de différents types.

### Threads et Process

Problème rencontré : Si on lance deux fois le même process, ils partagent les mêmes instances des rules (pas de soucis), des AtPredicate (idem), et des At (ouch). Donc si on lance deux fois le même process, et que le premier stop ses At, alors le second aura ses At stoppés également, puisque ce sont les mêmes objets.

Solution, faire un nouveau thread à chaque process --> Fonctionne pas mieux, mais au moins, les process marchent en parallèle

Les Process marchent de la même manière qu'il fonctionnaient auparavant, globalement.

La Node Process (ou ProcessCreator) va créer un process et le stocker dans le FunctionRegistry. La Node ProcessExecutor est appellée lorsqu'on invoque le process.
Elle est responsable de l'instanciation du process en tant que thread. ProcessRunner

#### Comment créer un thread ?

Pour créer un thread, on ne peut pas juste faire `new Thread(myRunnable).start()`. Sinon, le PolyglotEngine va râler. Alors, il faut créer un thread grâce à Truffle. En utilisant la variable Env
qui est passée au contexe à l'instanciation du language (voir `IniLanguage.createContext(Env env)`), on va faire `env.createThread(myRunnable, env.getContext())`. 
Le deuxième argument signifie que le Thread va fonctionner avec le même contexte du PolyglotEngine. Ce n'est peut être pas obligatoire, mais je ne suis pas sûr.

Ensuite, il faut se souvenir des threads que tu as commencé, comme ça à la fin, tu peux tous les join dans `IniLanguage.finalizeContext()`.

Oh, et il faut bien utiliser `Thread.start()` et non `Thread.run()`. Ce dernier va juste se contenter de lancer la méthode run du thread, sans l'enregistrer dans le PolyglotEngine (et en plus, ça ne s'executera pas en parallèle)

Voilà ce qui arrivent si on démarre mal les thread `java.lang.IllegalStateException: The language did not complete all polyglot threads but should have: [Thread[Polyglot-INI-0,5,main]]`.
Dans ce cas, il faut aller regarder la création du Thread Polyglot-Ini-0

## Quelques fonctionnalités utiles

### Frame

Les `Frames` et leurs enfants les `VirtualFrame` et les `MaterializedFrame` sont des structures de données données par Truffle qui servent de Stack d'execution. Elles permettent d'y stocker des variables pour pouvoir les retrouver plus tard. Et elles sont construites de manière optimisée par rapport à GraalVM.

Les valeurs sont stockées dans des frames avec des clés, ces clés sont des `FrameSlot`. On peut trouver les `FrameSlot` grâce au `FrameDescriptor`

Les `Frame` possèdent un `FrameDescriptor` qui permettra de savoir quels sont les `FrameSlot` utilisés, et quels types de valeurs sont stockées dans la `Frame`

La VirtualFrame, c'est la concretisation de l'interface Frame.  
La MaterializedFrame, c'est une VirtualFrame qu'on peut stocker dans des attributs ou cast en objet, ou passer dans un for loop sans aucun soucis. Seulement, cette frame matérialisée et plus lente que sa soeur virtuele

### IniContext

Le language INI possède une classe IniContext qui permet de donner notamment :

- La frame globale (root) de l'execution.
- Les sorties et entrées pour l'écriture et lecture (pratique pour tester).
- Le registre des fonctions

### lookupContextReference

La méthode `lookupContextReference` permet, comme son nom l'indique, d'avoir la référence du contexte (IniContext) actuel, et grâce à cette référence, d'avoir le contexte tout court !
Cette méthode est pratique si on veut utiliser le functionRegistry, ou alors la sortie ou l'entrée trouvable dans le IniContext.

Elle est utilisée notamment dans `Function`, et dans `Invocation`

### Directives to the Compiler

`CompilerDirectives.transferToInterpreter`, `CompilerDirectives.transferToInterpreterAndInvalidate` et `@TruffleBoundary` sont toutes des manières d'annoncer au compilateur de ne PAS compiler le code qui suit, et de laisser faire l'interprète.

C'est obligatoire pour les endroit où il y a des opérations concernant des HashMap (sinon, on a un joli message d'erreur, car le compilateur ne sait pas gérer ça). Mais le reste du temps, c'est pratique pour certaines méthodes précises qui peuvent ralentir la compilation car elles sont complexe. Comme `println` par exemple.

#### @CachedContext

l'annotation `@CachedContext(IniLanguage.class)` avant un paramètre dans une fonction fait exactement la même opération, sauf que le contexte est mit en cache.

```java
    @Specialization
    public Number println(Number value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrintln(context.getOut(), value);
        return value;
    }
```

### @ExplodeLoop

Les annotations `@ExplodeLoop` peuvent être placées sur des méthodes qui contiennent des boucles avec un nombre d'itération connu à la compilation (celle du JIT compiler, pas la compilation du javac), ce qui est le cas des variables `final` ou `@CompilationFinal`.
Si le compilateur graal rencontre une telle annotation, il va "dérouler" la boucle, et "inliner" toutes les itérations de la boucle côte à côte.
Théoriquement, cet inlining permet d'accélerer l'execution, légèrement, puisque il n'y a plus à gerer la boucle.

Par ailleurs, cette annotation est indispensable sur certaines méthodes où il y a un passage de `VirtualFrame` à l'intérieur de la boucle.
Par exemple, la méthode `execute` de `IniRootNode`. Elle contient une boucle `for` qui execute les différentes `bodyNodes`. Lorsqu'elle execute une bodyNode, elle leur passe sa virtualFrame.
Et bien, il se trouve que dans Truffle, si tu fais une boucle for, la virtualFrame ne peut pas être utilisée à l'interieur, a moins d'être matérialisée avec `frame.materialize()`. Mais si c'est le cas, elle perd de sa vitesse.
Alors, grâce à `@ExplodeLoop`, il n'y a techniquement plus de boucle for, ce qui permet d'utiliser la VirtualFrame tranquilement.

### @Child et @Children

Dans un AST, chaque Node peut posséder une ou plusieurs Node enfants. Ce concept est représenté dans Truffle grâce à des annotations `@Child` et `@Children`.

Lorsqu'une Node possède des enfants, ils doivent être déclarés dans ses champs (attributs). Et l'annotation `@Child` doit accompagner la déclaration. Si c'est une array de Nodes, c'est `@Children` qui doit accompagner la déclaration.

Ces annotations permettent à Truffle de construire en interne l'Abstract Syntax Tree, et de faire des optimisations dessus.
Quand une Node fait partie de l'AST (qu'elle a été découverte par Truffle grâce notamment aux annotations `@Child` et `@Children`), on dit qu'elle est adoptée.
Il faut faire assez attention à ce que toutes les Nodes de l'AST soient adoptées, sinon, il peut en découler des bugs.

L'Adoption des Nodes est indispensable pour l'utilisation de certaines méthodes. C'est le cas par exemple de la méthode `lookupContextReference(IniLanguage.class)` et de l'annotation `@CachedContext`. Ces méthodes et annotations étant indispensable au bon fonctionnement de INI, il n'est pas possible de retirer les annotations `@Child` et `@Children` afin de comparer les performances avec et sans.

## Quelles fonctionnalités ralentissent ou accélèrent INI ?

Les benchmarks sont réalisés grâce au script bench_fib

### @Compilation Final

"Marks fields that should be considered final for a Truffle compilation although they are not
final while executing in the interpreter. If the field type is an array type, the compiler
considers reads with a constant index as constants."

Si jamais on veut modifier un field qui est `@CompilationFinal`, il faut utiliser la directive
`CompilerDirectives.transferToInterpreterAndInvalidate()` qui va signaler au compilateur de
ne pas compiler le code qui suit cette commande et de laisser ce travail à l'interprète (`transferToInterpreter`).
Mais aussi d'*invalider* le code qui suit si cette commande est atteinte (`AndInvalidate`).
Cela signifie que tout le code de la classe sera recompilé en prenant en compte le changement du field `@CompilationFinal`.

L'attribut ou la variable qui possède CompilationFinal va pouvoir être traitée aux yeux du compilateur comme finale.
Donc il va pouvoir prendre quelques raccourcis dans sa compilation.
Le tout est de savoir si le coût du `transferToInterpreterAndInvalidate()` est moins haut que le gain fait avec l'optimisation.

#### Avec

Average time over 70 runs : 492  
Standard deviation = 14.16333294108417123741  
Error margin = 3.38568414694240827924  
Confidence interval = [488.61431585305759172076 ; 495.38568414694240827924]  

#### Sans

Average time over 70 runs : 513  
Standard deviation = 17.24363236510385426794  
Error margin = 4.12201654632332417400  
Confidence interval = [508.87798345367667582600 ; 517.12201654632332417400]  

#### Conclusions

On remarque que en effet, c'est significativement plus rapide avec les annotations.

### IndirectCallNode, DirectCallNode ou DispatchCallNode

#### RootNode

Dans Truffle, une fonction doit posséder une RootNode. C'est une node qui est à
la racine de toutes les autres de la fonction, et qui en l'executant va executer
tout le body de la fonction.

<details>
    <summary>Documentation de la RootNode de Truffle</summary>

##### Represents

the root node in a Truffle AST. The root node is a node that allows to be executed using a frame instance created bythe framework. Please note that the RootNode should not be executed directly but using CallTarget.call(Object). The structure of the frame is provided by the frame descriptor passed in the constructor. A root node has always a null parent and cannot be replaced. 

##### Construction

The root node can be constructed with a language implementation if it isavailable. The language implementation instance is obtainable while TruffleLanguage.createContext(Env) or TruffleLanguage.parse(ParsingRequest) isexecuted. If no language environment is available, then null can be passed. Pleasenote that root nodes with null language are considered not instrumentable and don'thave access to its public language information. 

##### Execution

In order to execute a root node, a call target needs to be created using TruffleRuntime.createCallTarget(RootNode). This allows the runtime system to optimize theexecution of the AST. The CallTarget can either be called directly from runtime code or direct and indirect call nodes can be created, inserted in a child field and called. The use of direct call nodes allows the frameworkto automatically inline and further optimize call sites based on heuristics.  
After several calls to a call target or call node, the root node might get compiled using partialevaluation. The details of the compilation heuristic are unspecified, therefore the Truffleruntime system might decide to not compile at all.  

</details>

#### Les CallNodes

Truffle deux manières différentes de gérer les appels à une RootNode (à une fonction).

1. IndirectCallNode

On peut faire la méthode simple, qui est utiliser une `IndirectCallNode` que l'on crée dans le constructeur de la classe `Invocation`
et à qui on passe le `callTarget` (la target representant la RootNode) lors
de l'appel à `IndirectCallNode.call()`.
Donc ça ressemblera à ça :

```java
public class Invocation extends AstExpression

    @Child public IndirectCallNode callNode;
    public Invocation(String name, List<AstExpression> arguments) {
        super();
        [...]
        this.callNode = Truffle.getRuntime().createIndirectCallNode();
    }

    public Object executeGeneric(VirtualFrame virtualFrame) {
        [...]
        return this.callNode.call(cachedFunction.callTarget, argumentValues);
    }
```

Voici les temps obtenus avec `IndirectCallNode`

Average time over 70 runs : 485  
Standard deviation = 11.53751644481106316250  
Error margin = 2.75799394710084488234  
Confidence interval = [482.24200605289915511766 ; 487.75799394710084488234]  

2. DirectCallNode

Ou alors, si le CallTarget est le même à chaque appel de cette Node `Invocation`, on peut 
aussi utiliser `DirectCallNode`. Dans ce cas là, au lieu de donner la `callTarget` à l'appel,
on va la donner à la construction de la Node.
La différence dans l'execution, c'est que Truffle, connaissant déjà la `callTarget` va pouvoir, optimiser le langage en *inlinant* l'AST correspondant à la fonction avec l'AST parent (l'AST actuel). En théorie, ça nous donnera une certaine amélioration de la vitesse

Et ça donnera quelque chose comme ça :

```java
public class Invocation extends AstExpression

    @Child public DirectCallNode callNode;
    public Invocation(String name, List<AstExpression> arguments) {
        super();
        [...]
    }

    public Object executeGeneric(VirtualFrame virtualFrame) {
        [...]
        if (this.callNode == null){
            CompilerDirectives.transferToInterpreterAndInvalidate();
            /* Insert rewrites the AST tree, and inlines the function in the current tree*/
            this.callNode = this.insert(Truffle.getRuntime().createDirectCallNode(cachedFunction.callTarget));
        }
        if (cachedFunction.callTarget != this.callNode.getCallTarget()) {
            /* We do not know yet how to modify effectively a DirectCallNode*/
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new UnsupportedOperationException("Need to implement a proper inline cache.");
        }
        /* The callTarget is already known, so we don't pass it as an argument here*/
        return this.callNode.call(argumentValues);
    }
```

Voici les temps obtenus. On peut constater que c'est étonnement moins bien qu'avec `IndirectCallNode`. C'est sûrement dû au fait que l'appel à `insert` est très lourd en temps,
et que l'optimisation gagnée n'en vaut pas la peine.

Average time over 70 runs : 508  
Standard deviation = 12.73689803007668851814  
Error margin = 3.04470098394450765500  
Confidence interval = [504.95529901605549234500 ; 511.04470098394450765500]  

Le soucis avec `DirectCallNode`, c'est qu'on n'a pas encore de moyens pour changer de callNode, si jamais par exemple une fonction est redéfinie. C'est là qu'arrivent les `DispatchNodes`

3. `DispatchNode`

Les `DispatchNode` sont en fait quatre Nodes qui vont se spécialiser en DirectCallNode ou IndirectCallNode en fonction du besoin.

Average time over 70 runs : 508  
Standard deviation = 11.54556191789728353020  
Error margin = 2.75991718302250097476  
Confidence interval = [505.24008281697749902524 ; 510.75991718302250097476]  

### Profiling or not profiling ?

#### With profiling

Average time over 70 runs : 492  
Standard deviation = 18.97065402893938935570  
Error margin = 4.53485368663462762764  
Confidence interval = [487.46514631336537237236 ; 496.53485368663462762764]  

#### Without

Average time over 70 runs : 482  
Standard deviation = 9.47100839404125963844    
Error margin = 2.26400403836086072346  
Confidence interval = [479.73599596163913927654 ; 484.26400403836086072346]  

Un seul endroit avec du profiling --> Pas très utile.
Mais pourra être utile plus tard à d'autres endroits. Tester utilité avec la même méthode.


### @Child et @Children

ShortCircuitNode, Assignment, CaseStatement, ~~Function~~, IniRootNode, Invocation, ReturnStatement, Rule

Sans ces annotations, le "lookupContextReference" ne fonctionne pas sur les fonctions qui l'utilisent.

```
java.lang.IllegalStateException: Node must be adopted before a reference can be looked up.
```

C'est dû au fait que @Child et @Children, quand ils sont bien mit, indiquent à Truffle, la structure de l'AST.
Si on les retire, Truffle ne peut pas construire un AST.
Et je pense que dans la structure interne du programme, `lookupContextReference` fait référence à l'AST.

Si jamais je n'utilisais pas `lookupContextReference`, je pense que je pourrais sûrement retirer toutes ces annotations, et comparer la vitesse. (Malheureusement, je n'ai pas eu le temps de le faire maintenant)

### Les @Specialization TODO TESTER SANS SPECIALISATION

#### Une seule fois fib(30)

Average time over 200 runs : 640
Standard deviation = 26.42811003458249566094
Error margin = 3.73749916387950513746
Confidence interval = [636.26250083612049486254 ; 643.73749916387950513746]


#### Deux fois fib(30)

Average time over 200 runs : 504
Standard deviation = 58.73806261701180760182
Error margin = 8.30681647804981905632
Confidence interval = [495.69318352195018094368 ; 512.30681647804981905632]

#### Trois fois

Average time over 100 runs : 478
Standard deviation = 9.79948978263664500668
Error margin = 1.95989795652732900132
Confidence interval = [476.04010204347267099868 ; 479.95989795652732900132]

#### Quatre fois

Average time over 100 runs : 476
Standard deviation = 10.37496987947434958720
Error margin = 2.07499397589486991744
Confidence interval = [473.92500602410513008256 ; 478.07499397589486991744]

#### Cinq fois

Average time over 100 runs : 476
Standard deviation = 13.56097341638866276853
Error margin = 2.71219468327773255370
Confidence interval = [473.28780531672226744630 ; 478.71219468327773255370]


### Listes

#### Avant une éventuelle optimisation

Average time over 50 runs : 7
Standard deviation = 2.69814751264640829311
Error margin = .76315136113355651932
Confidence interval = [6.23684863886644348068 ; 7.76315136113355651932]

En faisant dix fois fibonacci :

Average time over 50 runs : 19
Standard deviation = 4.07430975749267252494
Error margin = 1.15238882327103467964
Confidence interval = [17.84761117672896532036 ; 20.15238882327103467964]

Ce programme ne prend pas assez de temps... Il nous faudrait quelque chose de plus gourmand, comme un bubble sort par exemple ! (TODO)

## Règles

Evenements règles.
Réactif plutôt que de checker à chaque fois eventListener

Remplacer atEvery par un consume en deux temps
un consume qui consume dans un channel
et un producer qui produit une data dans le channel tout les tick.

Se focaliser sur produce et consume.