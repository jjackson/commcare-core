package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathStringLengthFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    override fun evalBody(
        model: DataInstance<*>?, evalContext: EvaluationContext, evaluatedArgs: Array<Any>): Any {
        val s = FunctionUtils.toString(evaluatedArgs[0])
        return if (s == null) {
            0.0
        } else {
            s.length.toDouble()
        }
    }

    companion object {
        @JvmField
        val NAME = "string-length"
        private const val EXPECTED_ARG_COUNT = 1
    }
}
