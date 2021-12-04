December 4, 2021

## Purpose

Run tests in one terminal when you save changes in another.

## Background

Prettier is an opinionated code formatter where you only decide two
With fast tests, you can re-run the entire suite whenever a change is written
to disk. This gives you quick feedback when developing, and also encourages
you to keep the code compiling. :)

To do this, you need a utility that will run `make test` when one of a set of
files change. `entr` is one such utility that has the advantage of not polling.

## Steps

1. **CHANGE** your current working directory to where you store github sources.

1. **CLONE** the `entr` source repository. `git clone git@github.com:eradman/entr.git`

1. **CHANGE** your current directory to entr. `cd entr`

1. **CHECKOUT** the latest stable release, 5.0 as of this writing. `git checkout 5.0`

1. **CONFIGURE** and **TEST**. `./configure && make test`

1. **INSTALL** to /usr/local (by default on OSX). `make install`
