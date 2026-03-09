package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.XPathNodeset
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathConcatFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    @Throws(XPathSyntaxException::class)
    override fun validateArgCount() {
        // zero or more arguments
    }

    override fun evalBody(
        model: DataInstance<*>?, evalContext: EvaluationContext, evaluatedArgs: Array<Any>): Any {
        @Suppress("UNCHECKED_CAST")
        return if (args.size == 1 && evaluatedArgs[0] is XPathNodeset) {
            XPathJoinFunc.join("", (evaluatedArgs[0] as XPathNodeset).toArgList() as Array<Any>)
        } else {
            XPathJoinFunc.join("", evaluatedArgs)
        }
    }

    companion object {
        @JvmField
        val NAME = "concat"
        // zero or more arguments
        private const val EXPECTED_ARG_COUNT = -1
    }
}
