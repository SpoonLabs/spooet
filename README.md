# Spooet

[Spoon](http://spoon.gforge.inria.fr/) printer implemented with open source project JavaPoet. Use to generate beautiful java code.

## Usage

Spooet has its custom Spoon launcher to use Spoon and to generate source code given at Spoon. So you must to use this launcher to generate source code with JavaPoet.

```java
final String[] args = new String[] {
    "-i", "src/main/java",
    "-o", "target/spooned",
    "--source-classpath", systemClassPath,
    "--compile"
};

PoetLauncher.main(args);
```

## Download

To use spooet, you first clone this repository and install it on your maven local repository.

## License

Copyright Inria, all rights reserved.