package ch.guytomoki.challenge.document;

import ch.guytomoki.challenge.MappersConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(config = MappersConfig.class)
public interface DocumentMapper {

	DocumentRespDto toRespDto(Document document);

	@Mapping(target = "fileContent", source = "fileContent", qualifiedByName = "decode")
	Document toEntity(DocumentRequestDto dto);

	@Named("decode")
	default byte[] decode(String encodedString) {
		return encodedString != null ? Base64.getDecoder().decode(encodedString) : null;
	}
}
