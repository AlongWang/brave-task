package brave.spring.scheduled;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import org.springframework.core.task.TaskDecorator;

public class TaskTracingDecorator implements TaskDecorator {
    final Tracing tracing;
    final CurrentTraceContext currentTraceContext;
    final Tracer tracer;

    public static TaskTracingDecorator create(Tracing tracing) {
        return new TaskTracingDecorator(tracing);
    }

    TaskTracingDecorator(Tracing tracing) {
        this.tracing = tracing;
        this.currentTraceContext = tracing.currentTraceContext();
        this.tracer = tracing.tracer();
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        TraceContext maybeParent = currentTraceContext.get();
        return () -> {
            Span span;
            if (maybeParent == null) {
                span = tracer.nextSpan(TraceContextOrSamplingFlags.EMPTY);
            } else {
                span = tracer.newChild(maybeParent);
            }

            if (!tracing.isNoop()) {
                span.start();
            }
            try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
                runnable.run();
            } catch (Exception e) {
                span.error(e);
                throw e;
            } finally {
                span.finish();
            }
        };
    }
}
