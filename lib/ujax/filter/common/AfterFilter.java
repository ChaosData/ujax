package lib.ujax.filter.common;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import java.util.function.*;

@FunctionalInterface
public interface AfterFilter
extends BiConsumer<ContainerRequestContext,ContainerResponseContext> { }