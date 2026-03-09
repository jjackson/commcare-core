package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance

class XPathArithExpr : XPathBinaryOpExpr {

    @Suppress("unused")
    constructor() // for deserialization

    constructor(op: Int, a: XPathExpression, b: XPathExpression) : super(op, a, b)

    override fun evalRaw(model: DataInstance<*>?, evalContext: EvaluationContext): Any {
        val aval = FunctionUtils.toNumeric(a.eval(model, evalContext))
        val bval = FunctionUtils.toNumeric(b.eval(model, evalContext))

        val result = when (op) {
            ADD -> aval + bval
            SUBTRACT -> aval - bval
            MULTIPLY -> aval * bval
            DIVIDE -> aval / bval
            MODULO -> aval % bval
            else -> 0.0
        }
        return result
    }

    override fun toString(): String {
        val sOp = when (op) {
            ADD -> "+"
            SUBTRACT -> "-"
            MULTIPLY -> "*"
            DIVIDE -> "/"
            MODULO -> "%"
            else -> null
        }
        return super.toString(sOp)
    }

    override fun toPrettyString(): String {
        val prettyA = a.toPrettyString()
        val prettyB = b.toPrettyString()
        val opString = when (op) {
            ADD -> " + "
            SUBTRACT -> " - "
            MULTIPLY -> " * "
            DIVIDE -> " div "
            MODULO -> " mod "
            else -> return "unknown_operator($prettyA, $prettyB)"
        }
        return prettyA + opString + prettyB
    }

    override fun equals(other: Any?): Boolean {
        return (this === other) ||
                ((other is XPathArithExpr) && binOpEquals(other))
    }

    companion object {
        @JvmField
        val ADD = 0
        @JvmField
        val SUBTRACT = 1
        @JvmField
        val MULTIPLY = 2
        @JvmField
        val DIVIDE = 3
        @JvmField
        val MODULO = 4
    }
}
