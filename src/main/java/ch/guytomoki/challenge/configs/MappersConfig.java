package ch.guytomoki.challenge.configs;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = "spring")
public interface MappersConfig {

}