package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.condition.RequestAbandonedException
import org.javarosa.core.model.condition.pivot.UnpivotableExpressionException
import org.javarosa.core.model.instance.AbstractTreeElement
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.core.model.instance.TreeReference
import org.javarosa.core.services.Logger
import org.javarosa.core.util.externalizable.Externalizable
import org.javarosa.model.xform.DataModelSerializer
import org.javarosa.xpath.XPathNodeset
import org.kxml2.io.KXmlSerializer

import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.Vector

abstract class XPathExpression : InFormCacheableExpr(), Externalizable {

    fun eval(evalContext: EvaluationContext): Any {
        return eval(evalContext.getMainInstance(), evalContext)
    }

    /**
     * Evaluate this expression, potentially capturing any additional
     * information about the evaluation.
     *
     * @return The result of this expression evaluated against the provided context
     */
    open fun eval(model: DataInstance<*>?, evalContext: EvaluationContext): Any {
        evalContext.openTrace(this)
        if (Thread.interrupted()) {
            throw RequestAbandonedException()
        }

        val value: Any
        var fromCache = false
        if (isCached(evalContext)) {
            value = getCachedValue()!!
            fromCache = true
        } else {
            value = evalRaw(model, evalContext)
            cache(value, evalContext)
        }

        evalContext.reportTraceValue(value, fromCache)
        evalContext.closeTrace()

        return value
    }

    /**
     * Perform the raw evaluation of this expression producing an
     * appropriately typed XPath output with no side effects
     *
     * @return The result of this expression evaluated against the provided context
     */
    protected abstract fun evalRaw(model: DataInstance<*>?, evalContext: EvaluationContext): Any

    fun pivot(
        model: DataInstance<*>,
        evalContext: EvaluationContext
    ): Vector<Any> {
        try {
            val pivots = Vector<Any>()
            this.pivot(model, evalContext, pivots, evalContext.contextRef!!)
            return pivots
        } catch (uee: UnpivotableExpressionException) {
            // Rethrow unpivotable (expected)
            throw uee
        } catch (e: Exception) {
            // Pivots aren't critical, if there was a problem getting one, log the exception so we can fix it, and then just report that.
            Logger.exception("Error during expression pivot", e)
            throw UnpivotableExpressionException(e.message)
        }
    }

    /**
     * Pivot this expression, returning values if appropriate, and adding any pivots to the list.
     *
     * @param model       The model to evaluate the current expression against
     * @param evalContext The evaluation context to evaluate against
     * @param pivots      The list of pivot points in the xpath being evaluated. Pivots should be added to this list.
     * @param sentinal    The value which is being pivoted around.
     * @return null - If a pivot was identified in this expression
     * sentinal - If the current expression represents the sentinal being pivoted
     * any other value - The result of the expression if no pivots are detected
     * @throws UnpivotableExpressionException If the expression is too complex to pivot
     */
    @Throws(UnpivotableExpressionException::class)
    open fun pivot(
        model: DataInstance<*>,
        evalContext: EvaluationContext,
        pivots: Vector<Any>,
        sentinal: Any
    ): Any? {
        return eval(model, evalContext)
    }

    /*======= DEBUGGING ========*/
    // should not compile onto phone

    /* print out formatted expression tree */

    private var indent: Int = 0

    private fun printStr(s: String) {
        for (i in 0 until 2 * indent) {
            kotlin.io.print(" ")
        }
        kotlin.io.println(s)
    }

    fun printParseTree() {
        indent = -1
        printNode(this)
    }

    fun print(o: Any) {
        printNode(o)
    }

    private fun printNode(o: Any) {
        indent += 1

        if (o is XPathStringLiteral) {
            printStr("strlit {${o.s}}")
        } else if (o is XPathNumericLiteral) {
            printStr("numlit {${o.d}}")
        } else if (o is XPathVariableReference) {
            printStr("var {${o.id}}")
        } else if (o is XPathArithExpr) {
            val op = when (o.op) {
                XPathArithExpr.ADD -> "add"
                XPathArithExpr.SUBTRACT -> "subtr"
                XPathArithExpr.MULTIPLY -> "mult"
                XPathArithExpr.DIVIDE -> "div"
                XPathArithExpr.MODULO -> "mod"
                else -> null
            }
            printStr("$op {{")
            printNode(o.a)
            printStr(" } {")
            printNode(o.b)
            printStr("}}")
        } else if (o is XPathBoolExpr) {
            val op = when (o.op) {
                XPathBoolExpr.AND -> "and"
                XPathBoolExpr.OR -> "or"
                else -> null
            }
            printStr("$op {{")
            printNode(o.a)
            printStr(" } {")
            printNode(o.b)
            printStr("}}")
        } else if (o is XPathCmpExpr) {
            val op = when (o.op) {
                XPathCmpExpr.LT -> "lt"
                XPathCmpExpr.LTE -> "lte"
                XPathCmpExpr.GT -> "gt"
                XPathCmpExpr.GTE -> "gte"
                else -> null
            }
            printStr("$op {{")
            printNode(o.a)
            printStr(" } {")
            printNode(o.b)
            printStr("}}")
        } else if (o is XPathEqExpr) {
            val op = if (o.op == XPathEqExpr.EQ) "eq" else "neq"
            printStr("$op {{")
            printNode(o.a)
            printStr(" } {")
            printNode(o.b)
            printStr("}}")
        } else if (o is XPathUnionExpr) {
            printStr("union {{")
            printNode(o.a)
            printStr(" } {")
            printNode(o.b)
            printStr("}}")
        } else if (o is XPathNumNegExpr) {
            printStr("neg {")
            printNode(o.a)
            printStr("}")
        } else if (o is XPathFuncExpr) {
            if (o.args.isEmpty()) {
                printStr("func {${o.name}, args {none}}")
            } else {
                printStr("func {${o.name}, args {{")
                for (i in o.args.indices) {
                    printNode(o.args[i])
                    if (i < o.args.size - 1) {
                        printStr(" } {")
                    }
                }
                printStr("}}}")
            }
        } else if (o is XPathPathExpr) {
            val init = when (o.initContext) {
                XPathPathExpr.INIT_CONTEXT_ROOT -> "root"
                XPathPathExpr.INIT_CONTEXT_RELATIVE -> "relative"
                XPathPathExpr.INIT_CONTEXT_EXPR -> "expr"
                else -> null
            }

            printStr("path {init-context:$init,")

            if (o.initContext == XPathPathExpr.INIT_CONTEXT_EXPR) {
                printStr(" init-expr:{")
                printNode(o.filtExpr!!)
                printStr(" }")
            }

            if (o.steps.isEmpty()) {
                printStr(" steps {none}")
                printStr("}")
            } else {
                printStr(" steps {{")
                for (i in o.steps.indices) {
                    printNode(o.steps[i])
                    if (i < o.steps.size - 1) {
                        printStr(" } {")
                    }
                }
                printStr("}}}")
            }
        } else if (o is XPathFilterExpr) {
            printStr("filter-expr:{{")
            printNode(o.x)

            if (o.predicates.isEmpty()) {
                printStr(" } predicates {none}}")
            } else {
                printStr(" } predicates {{")
                for (i in o.predicates.indices) {
                    printNode(o.predicates[i])
                    if (i < o.predicates.size - 1) {
                        printStr(" } {")
                    }
                }
                printStr(" }}}")
            }
        } else if (o is XPathStep) {
            val axis = XPathStep.axisStr(o.axis)
            val test = o.testStr()

            if (o.predicates.isEmpty()) {
                printStr("step {axis:$axis test:$test predicates {none}}")
            } else {
                printStr("step {axis:$axis test:$test predicates {{")
                for (i in o.predicates.indices) {
                    printNode(o.predicates[i])
                    if (i < o.predicates.size - 1) {
                        printStr(" } {")
                    }
                }
                printStr("}}}")
            }
        }

        indent -= 1
    }

    // Make sure hashCode and equals are implemented by child classes.
    // If you override one, it is best practice to also override the other.
    abstract override fun hashCode(): Int

    abstract override fun equals(other: Any?): Boolean

    /**
     * @return a best-effort for the cannonical representation
     * of this expression. May not be one-to-one with the original
     * text, and may not be semantically complete, but should ideally
     * provide a human with a clear depiction of the expression.
     */
    abstract fun toPrettyString(): String

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun serializeResult(value: Any, output: OutputStream) {
            if (value is XPathNodeset && !isLeafNode(value)) {
                serializeElements(value, output)
            } else {
                output.write(FunctionUtils.toString(value).toByteArray(StandardCharsets.UTF_8))
            }
        }

        private fun isLeafNode(value: XPathNodeset): Boolean {
            val refs: Vector<TreeReference> = value.getReferences() ?: return false
            if (refs.size != 1) {
                return false
            }

            val instance: DataInstance<*> = value.getInstance()!!
            val treeElement: AbstractTreeElement = instance.resolveReference(refs[0])!!
            return treeElement.getNumChildren() == 0
        }

        @Throws(IOException::class)
        private fun serializeElements(nodeset: XPathNodeset, output: OutputStream) {
            val serializer = KXmlSerializer()

            try {
                serializer.setOutput(output, "UTF-8")
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            val s = DataModelSerializer(serializer)

            val instance: DataInstance<*> = nodeset.getInstance()!!
            val refs: Vector<TreeReference> = nodeset.getReferences() ?: return

            for (ref in refs) {
                val treeElement: AbstractTreeElement = instance.resolveReference(ref)!!
                s.serializeNode(treeElement)
            }
            serializer.flush()
        }
    }
}
