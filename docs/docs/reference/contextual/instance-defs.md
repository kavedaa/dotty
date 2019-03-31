---
layout: doc-page
title: "Context Instances"
---

Context instance definitions define context-scoped values of given types
that serve for synthesizing arguments to [context-given parameters](./inferable-params.html). Example:

```scala
trait Ord[T] {
  def compare(x: T, y: T): Int
  def (x: T) < (y: T) = compare(x, y) < 0
  def (x: T) > (y: T) = compare(x, y) > 0
}

context IntOrd for Ord[Int] {
  def compare(x: Int, y: Int) =
    if (x < y) -1 else if (x > y) +1 else 0
}

context ListOrd[T] given (ord: Ord[T]) for Ord[List[T]] {
  def compare(xs: List[T], ys: List[T]): Int = (xs, ys) match {
    case (Nil, Nil) => 0
    case (Nil, _) => -1
    case (_, Nil) => +1
    case (x :: xs1, y :: ys1) =>
      val fst = ord.compare(x, y)
      if (fst != 0) fst else xs1.compareTo(ys1)
  }
}
```
This code defines a trait `Ord` and two context instance definitions. `IntOrd` defines
a context instance for the type `Ord[Int]` whereas `ListOrd[T]` defines context
instances of `Ord[List[T]]` for all types `T` that come with a `Ord[T]` context instance themselves.
The `given` clause in `ListOrd` defines a [context-given parameter](./inferable-params.html).
Context-given parameters are further explained in the next section.

## Anonymous Context Instances

The name of a context instance can be left out. So the context instance definitions
of the last section can also be expressed like this:
```scala
context for Ord[Int] { ... }
context [T] given (ord: Ord[T]) for Ord[List[T]] { ... }
```
If the name of an instance is missing, the compiler will synthesize a name from
the type(s) in the `for` clause.

## Context Alias Instances

An context alias instance defines a context instance that is equal to some expression. 
```scala
context ctx for ExecutionContext = ExecutionContext.global
```
This creates an context instance `ctx` of type `ExecutionContext` that resolves to the right hand side `ExecutionContext.global`. Each time a context instance of `ExecutionContext` is demanded, the result of evaluating the right-hand side expression is returned.

Alias instances may be anonymous, e.g.
```scala
context for Position = enclosingTree.position
```
A context alias instance can have type and context-given parameters just like any other context instance definition, but it can only implement a single type.

## Syntax

Here is the new syntax of context instance definitions, seen as a delta from the [standard context free syntax of Scala 3](http://dotty.epfl.ch/docs/internals/syntax.html).
```
TmplDef          ::=  ...
                  |  ‘context’ InstanceDef
InstanceDef      ::=  [id] InstanceParams InstanceBody
InstanceParams   ::=  [DefTypeParamClause] {InferParamClause}
InferParamClause ::=  ‘given’ (‘(’ [DefParams] ‘)’ | ContextTypes)
InstanceBody     ::=  [‘for’ ConstrApp {‘,’ ConstrApp }] [TemplateBody]
                   |  ‘for’ Type ‘=’ Expr
ContextTypes     ::=  RefinedType {‘,’ RefinedType}
```
The identifier `id` can be omitted only if either the `for` part or the template body is present.
If the `for` part is missing, the template body must define at least one extension method.
