package com.github.viniciusfcf.ifood.cadastro.dto;

import com.github.viniciusfcf.ifood.cadastro.Restaurante;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface RestauranteMapper {
	
	@Mapping(target = "nome", source = "nomeFantasia")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dataCriacao", ignore = true)
	@Mapping(target = "dataAtualizacao", ignore = true)
	@Mapping(target = "localizacao.id", ignore = true) //localizacao.id, getter???
	public Restaurante toRestaurante(AdicionarRestauranteDTO dto);
	
	@Mapping(target = "nome", source = "nomeFantasia")
	public void toRestaurante(AtualizarRestauranteDTO dto, @MappingTarget Restaurante restaurante);
	
	
	@Mapping(target = "nomeFantasia", source = "nome")
	@Mapping(target = "dataCriacao", dateFormat = "dd/MM/yyyy HH:mm:ss") // Exemplo de formatação
	public RestauranteDTO toRestauranteDTO(Restaurante restaurante);
	
}
