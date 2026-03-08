package org.javarosa.core.model.utils

import org.javarosa.core.model.trace.EvaluationTrace
import org.javarosa.core.model.trace.EvaluationTraceReporter
import org.javarosa.core.model.trace.TraceSerialization

/**
 * Utility functions for instrumentation in the engine
 *
 * Created by ctsims on 7/6/2017.
 */
object InstrumentationUtils {

    @JvmStatic
    fun printAndClearTraces(reporter: EvaluationTraceReporter?, description: String) {
        printAndClearTraces(reporter, description, TraceSerialization.TraceInfoType.FULL_PROFILE)
    }

    /**
     * Prints out traces (if any exist) from the provided reporter with a description into sysout
     */
    @JvmStatic
    fun printAndClearTraces(
        reporter: EvaluationTraceReporter?,
        description: String,
        requestedInfo: TraceSerialization.TraceInfoType
    ) {
        if (reporter != null) {
            if (reporter.wereTracesReported()) {
                println(description)
            }

            for (trace in reporter.collectedTraces) {
                println("${trace.expression}: ${trace.value}")
                print(TraceSerialization.serializeEvaluationTrace(trace, requestedInfo, reporter.reportAsFlat()))
            }

            reporter.reset()
        }
    }

    /**
     * Prints out traces (if any exist) from the provided reporter with a description into sysout
     */
    @JvmStatic
    fun collectAndClearTraces(
        reporter: EvaluationTraceReporter?,
        description: String,
        requestedInfo: TraceSerialization.TraceInfoType
    ): String {
        var returnValue = ""
        if (reporter != null) {
            if (reporter.wereTracesReported()) {
                returnValue += description + "\n"
            }

            for (trace in reporter.collectedTraces) {
                returnValue += "${trace.expression}: ${trace.value}\n"
                returnValue += TraceSerialization.serializeEvaluationTrace(
                    trace, requestedInfo, reporter.reportAsFlat()
                )
            }

            reporter.reset()
        }
        return returnValue
    }

    @JvmStatic
    fun printExpressionsThatUsedCaching(reporter: EvaluationTraceReporter?, description: String) {
        if (reporter != null) {
            if (reporter.wereTracesReported()) {
                println(description)
            }

            for (trace in reporter.collectedTraces) {
                if (trace.evaluationUsedExpressionCache()) {
                    println("${trace.expression}: ${trace.value}")
                    println("    ${trace.cacheReport}")
                }
            }
        }
    }

    @JvmStatic
    fun printCachedAndNotCachedExpressions(reporter: EvaluationTraceReporter?, description: String) {
        if (reporter != null) {
            if (reporter.wereTracesReported()) {
                println(description)
            }

            val withCaching = mutableListOf<EvaluationTrace>()
            val withoutCaching = mutableListOf<EvaluationTrace>()
            for (trace in reporter.collectedTraces) {
                if (trace.evaluationUsedExpressionCache()) {
                    withCaching.add(trace)
                } else {
                    withoutCaching.add(trace)
                }
            }

            println("EXPRESSIONS NEVER CACHED: ${withoutCaching.size}")
            for (trace in withoutCaching) {
                println("${trace.expression}: ${trace.value}")
            }

            println("EXPRESSIONS CACHED: ${withCaching.size}")
            for (trace in withCaching) {
                println("${trace.expression}: ${trace.value}")
                println("    ${trace.cacheReport}")
            }

            reporter.reset()
        }
    }
}
