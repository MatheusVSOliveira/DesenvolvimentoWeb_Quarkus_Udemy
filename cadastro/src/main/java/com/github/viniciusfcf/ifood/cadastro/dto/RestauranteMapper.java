package com.github.viniciusfcf.ifood.cadastro.dto;

import com.github.viniciusfcf.ifood.cadastro.Restaurante;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface RestauranteMapper {
	
	@Mapping(target = "nome", source = "nomeFantasia")
	public Restaurante toRestaurante(AdicionarRestauranteDTO dto);
	
}
