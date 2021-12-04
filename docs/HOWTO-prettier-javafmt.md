December 4, 2021
How to install the Prettier formatter for Java

## Purpose

Define, in code, how Java code should be formatted.

## Background

Prettier is an opinionated code formatter where you only decide two
things: tabs or spaces and line widths, and has syntax engines for
many languages. See https://www.npmjs.com/package/prettier for
more information.

## Steps

_Example_: Download 14.17.6 LTS from https://nodejs.org/en/
and install npm and nodejs in /usr/local/bin.

1. **INSTALL** npm.

2. **VERIFY** /usr/local/bin is on your PATH.

3. **INITIALIZE** npm package file by running `npm init`.

4. **INSTALL** the prettier-plugin-java: `npm install prettier-plugin-java --save-dev`.

5. Run `npx prettier --check ."` to **VERIFY** install. Prettier
   will print a report on all files under your current directory that
   are not formatted correctly.
