package ch.guytomoki.challenge.signingRequest;

import ch.guytomoki.challenge.configs.MappersConfig;
import org.mapstruct.Mapper;

@Mapper(config = MappersConfig.class)
public interface SigningRequestMapper {

	SigningRequestRespDto toRespDto(SigningRequest signingRequest);
}
