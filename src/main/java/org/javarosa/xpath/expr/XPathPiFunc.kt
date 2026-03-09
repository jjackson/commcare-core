package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathPiFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, false)

    override fun evalBody(
        model: DataInstance<*>?,
        evalContext: EvaluationContext,
        evaluatedArgs: Array<Any>
    ): Any {
        return Math.PI
    }

    companion object {
        @JvmField
        val NAME = "pi"
        private const val EXPECTED_ARG_COUNT = 0
    }
}
