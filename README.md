<div align="center">
<img width="25%" src="https://github.com/hexaredecimal/Zulu/blob/main/assets/zulu.png" />
<h1>ZULU Programming Language</h1>
</div>

[![CI](https://github.com/elixir-lang/elixir/workflows/CI/badge.svg?branch=main)](https://app.circleci.com/pipelines/github/corgifist/barley-lang/10/workflows/45b5d058-5c12-49f8-a44a-0a7f76fdf0c9)

 ## Key features
    
 - Functional programming, manage data in functional style
 - Supports Java interoperability
 - PKG Manager
 - Pattern matching 
 -  - Variables (see `Pattern matching` in `Examples`)
 -  - Functions (see `Pattern matching` in `Examples`)
 -  - Case Expression `case [1, 2, 2] -> of [H :: T]: io:fwriteln("%s | %s", H, T). end.`
 - Multi-assignment
 - Null safety
 - Spawn process, send message and don't worry about errors in it
 - Generator jamming (eval GeneratorAST before running the program)
 - Easily distribute programs by using `dist` module
 - Usually, distributed program is very small in terms of memory
 - Built-in optimization (constant folding, propagation, expression simplification and more)
 - Two Zulu instances can talk to each other by sending signals (see `examples/chat.zulu`)
 - Clean syntax

Zulu is a interpreted erlang-like language based on JVM.

## Installation

<img width="25%" src="https://github.com/hexaredecimal/Zulu/blob/main/assets/zulu-warrior.png" />
Install the latest release and Java SE 21 and Apache Ant.

```sh
$ git clone git@github.com:hexaredecimal/Zulu.git
$ cd Zulu
$ ant
$ ./install.sh
```
After running these commands `Zulu` should be installed in the `.local/bin` directory. 
```sh
$ zulu help
```

## Usage
>> Project
```sh
$ zulu new
 Enter project name: <project_name>
$ cd code
$ nvim main.zulu
```
>> To compile a project just type `zulu` and it will build and run.

## Reference
[Barley](https://github.com/corgifist/barley-lang/tree/main) - Forked parent
[Erlang](https://en.wikipedia.org/wiki/Erlang_(programming_language))
[Java](https://en.wikipedia.org/wiki/Java_(programming_language))

