package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.XPathArityException
import org.javarosa.xpath.XPathNodeset
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathJoinChunkFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    @Throws(XPathSyntaxException::class)
    override fun validateArgCount() {
        if (args.size < 3) {
            throw XPathArityException(name, "at least three arguments", args.size)
        }
    }

    override fun evalBody(
        model: DataInstance<*>?,
        evalContext: EvaluationContext,
        evaluatedArgs: Array<Any>
    ): Any {
        @Suppress("UNCHECKED_CAST")
        return if (args.size == 3 && evaluatedArgs[2] is XPathNodeset) {
            join(
                evaluatedArgs[0],
                evaluatedArgs[1],
                (evaluatedArgs[2] as XPathNodeset).toArgList() as Array<Any>
            )
        } else {
            join(
                evaluatedArgs[0],
                evaluatedArgs[1],
                FunctionUtils.subsetArgList(evaluatedArgs as Array<Any?>, 2) as Array<Any>
            )
        }
    }

    companion object {
        @JvmField
        val NAME = "join-chunked"
        private const val EXPECTED_ARG_COUNT = -1

        /**
         * concatenate an abritrary-length argument list of string values together
         */
        @JvmStatic
        fun join(oSep: Any, oChunkSize: Any, argVals: Array<Any>): String {
            val sep = FunctionUtils.toString(oSep)
            val chunkSize = FunctionUtils.toInt(oChunkSize).toInt()
            val intermediateBuffer = StringBuilder()
            val outputBuffer = StringBuilder()

            for (argVal in argVals) {
                intermediateBuffer.append(FunctionUtils.toString(argVal))
            }

            for (i in 0 until intermediateBuffer.length) {
                if (i != 0 && i % chunkSize == 0) {
                    outputBuffer.append(sep)
                }
                outputBuffer.append(intermediateBuffer[i])
            }

            return outputBuffer.toString()
        }
    }
}
