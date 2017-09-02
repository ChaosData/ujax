package lib.ujax.filter.common;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.function.*;

@FunctionalInterface
public interface BeforeFilter
extends Consumer<ContainerRequestContext> { }