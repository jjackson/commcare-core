package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance

class XPathBoolExpr : XPathBinaryOpExpr {

    @Suppress("unused")
    constructor() // for deserialization

    constructor(op: Int, a: XPathExpression, b: XPathExpression) : super(op, a, b)

    override fun evalRaw(model: DataInstance<*>?, evalContext: EvaluationContext): Any {
        val aval = FunctionUtils.toBoolean(a.eval(model, evalContext))

        // short-circuiting
        if ((!aval && op == AND) || (aval && op == OR)) {
            return aval
        }

        val bval = FunctionUtils.toBoolean(b.eval(model, evalContext))

        val result = when (op) {
            AND -> aval && bval
            OR -> aval || bval
            else -> false
        }
        return result
    }

    override fun toString(): String {
        val sOp = when (op) {
            AND -> "and"
            OR -> "or"
            else -> null
        }
        return super.toString(sOp)
    }

    override fun toPrettyString(): String {
        val prettyA = a.toPrettyString()
        val prettyB = b.toPrettyString()
        val opString = when (op) {
            AND -> " and "
            OR -> " or "
            else -> return "unknown_operator($prettyA, $prettyB)"
        }
        return prettyA + opString + prettyB
    }

    override fun equals(other: Any?): Boolean {
        return (this === other) ||
                ((other is XPathBoolExpr) && binOpEquals(other))
    }

    companion object {
        @JvmField
        val AND = 0
        @JvmField
        val OR = 1
    }
}
