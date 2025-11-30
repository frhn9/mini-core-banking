package org.fd.mcb.modules.auth.model.mapper;

import org.fd.mcb.modules.auth.dto.response.UserSessionResponse;
import org.fd.mcb.modules.auth.model.entity.UserSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting UserSession entities to UserSessionResponse DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserSessionMapper {

    /**
     * Converts a UserSession entity to UserSessionResponse DTO.
     *
     * @param userSession the UserSession entity
     * @return UserSessionResponse DTO
     */
    @Mapping(target = "currentSession", ignore = true)
    UserSessionResponse toResponse(UserSession userSession);
}
