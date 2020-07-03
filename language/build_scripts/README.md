Fair warning
============

The Dockerfiles here are outdated.

To use a graalVM use the standard `oracle/graalvm-ce image`  

If you are on windows :

`docker run -it -v %cd%:/ini_volume --name graal-container --entrypoint "sh" oracle/graalvm-ce`

To use a openJDK container, either use the `build_openjdk_image` script
or use the official java image

It is much easier in the state of the current dockerfiles to use the official images.

# How to use the dockerfiles

Use `build_graalvm_image.bat [Version]` to build an image with ini and graalvm inside

To build an image with graalvm, ini AND maven (for test purposes), use
`build_graalvm_test_image_first.bat [Version]` the first time

and 
`build_graalvm_test_image_second.bat [Version]`

docker run -it --rm

docker run -it --rm -v %cd%:/ini_volume