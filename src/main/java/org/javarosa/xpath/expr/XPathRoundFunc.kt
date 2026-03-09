package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathRoundFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    override fun evalBody(
        model: DataInstance<*>?,
        evalContext: EvaluationContext,
        evaluatedArgs: Array<Any>
    ): Any {
        return Math.floor(FunctionUtils.toDouble(evaluatedArgs[0]) + 0.5)
    }

    companion object {
        @JvmField
        val NAME = "round"
        private const val EXPECTED_ARG_COUNT = 1
    }
}
