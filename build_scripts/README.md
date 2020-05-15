# How to use the dockerfiles

Use `build_graalvm_image.bat [Version]` to build an image with ini and graalvm inside

To build an image with graalvm, ini AND maven (for test purposes), use
`build_graalvm_test_image_first.bat [Version]` the first time

and 
`build_graalvm_test_image_second.bat [Version]`

docker run -it --rm

docker run -it --rm -v %cd%:/ini_volume