package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.XPathArityException
import org.javarosa.xpath.XPathNodeset
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathJoinFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    @Throws(XPathSyntaxException::class)
    override fun validateArgCount() {
        if (args.size < 1) {
            throw XPathArityException(name, "at least one argument", args.size)
        }
    }

    override fun evalBody(
        model: DataInstance<*>?,
        evalContext: EvaluationContext,
        evaluatedArgs: Array<Any>
    ): Any {
        @Suppress("UNCHECKED_CAST")
        val argList: Array<Any> = when {
            args.size == 2 && evaluatedArgs[1] is XPathNodeset ->
                (evaluatedArgs[1] as XPathNodeset).toArgList() as Array<Any>
            args.size == 2 && evaluatedArgs[1] is Array<*> ->
                evaluatedArgs[1] as Array<Any>
            else ->
                FunctionUtils.subsetArgList(evaluatedArgs as Array<Any?>, 1) as Array<Any>
        }

        return join(evaluatedArgs[0], argList)
    }

    companion object {
        @JvmField
        val NAME = "join"
        private const val EXPECTED_ARG_COUNT = -1

        /**
         * concatenate an abritrary-length argument list of string values together
         */
        @JvmStatic
        fun join(oSep: Any, argVals: Array<Any>): String {
            val sep = FunctionUtils.toString(oSep)
            val sb = StringBuilder()

            for (i in argVals.indices) {
                sb.append(FunctionUtils.toString(argVals[i]))
                if (i < argVals.size - 1) {
                    sb.append(sep)
                }
            }

            return sb.toString()
        }
    }
}
