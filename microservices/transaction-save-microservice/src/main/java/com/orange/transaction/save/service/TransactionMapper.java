package com.orange.transaction.save.service;
import com.orange.helper.dto.TransactionDto;
import com.orange.helper.model.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mappings({
    })
    TransactionDto entityToDto(TransactionEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    TransactionEntity dtoToEntity(TransactionDto api);
}
