package com.github.viniciusfcf.ifood.cadastro;

import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.viniciusfcf.ifood.cadastro.dto.AdicionarPratoDTO;
import com.github.viniciusfcf.ifood.cadastro.dto.AdicionarRestauranteDTO;
import com.github.viniciusfcf.ifood.cadastro.dto.AtualizarPratoDTO;
import com.github.viniciusfcf.ifood.cadastro.dto.AtualizarRestauranteDTO;
import com.github.viniciusfcf.ifood.cadastro.dto.PratoMapper;
import com.github.viniciusfcf.ifood.cadastro.dto.RestauranteMapper;
import com.github.viniciusfcf.ifood.cadastro.infra.ConstraintViolationResponse;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "restaurante") //separa os verbos http na documentação pela tag = restaurante
@RolesAllowed("proprietario")
@SecurityScheme(securitySchemeName = "ifood-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/ifood/protocol/openid-connect/token")))
@SecurityRequirement(name = "ifood-oauth", scopes = {})
public class RestauranteResource {
	
	@Inject
	RestauranteMapper restauranteMapper;
	
	@Inject
	PratoMapper pratoMapper;
	
	
    @GET
    public List<Restaurante> buscar() {
        return Restaurante.listAll();
    }
    
    @POST
    @Transactional
    @APIResponse(responseCode = "201", description = "Caso restaurante seja cadastrado com sucesso")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ConstraintViolationResponse.class)))
    public Response adicionar(@Valid AdicionarRestauranteDTO dto) {
    	Restaurante restaurante = restauranteMapper.toRestaurante(dto);
    	restaurante.persist();
    	return Response.status(Status.CREATED).build();
    }
    
    @PUT
    @Path("{id}")
    @Transactional
    public void atualizar(@PathParam("id") Long id, AtualizarRestauranteDTO dto) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(id);
    	if(restauranteOp.isEmpty()) {
    		throw new NotFoundException();
    	}
    	Restaurante restaurante = restauranteOp.get();
    	//passagem de referência para atualização
    	restauranteMapper.toRestaurante(dto, restaurante);//restaurante.nome = dto.nome;
    	restaurante.persist();
    }
    
    @DELETE
    @Path("{id}")
    @Transactional
    public void deletar(@PathParam("id") Long id) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(id);
    	restauranteOp.ifPresentOrElse(Restaurante::delete, () -> {
    		throw new NotFoundException();
    		});
    	}
    
    
    //PRATOS - CRUD
    
    @GET
    @Path("{id-restaurante}/pratos")
    @Tag(name = "prato") //separa os verbos http na documentação pela tag = prato
    public List<Restaurante> buscarPratos(@PathParam("id-restaurante") Long idRestaurante) {
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
        if(restauranteOp.isEmpty()) {
        	throw new NotFoundException("Restaurante não existe");
        }
        return Prato.list("restaurante", restauranteOp.get());
    }
    
    @POST
    @Path("{id-restaurante}/pratos")
    @Transactional
    @Tag(name = "prato")
    public Response adicionar(@PathParam("id-restaurante") Long idRestaurante, AdicionarPratoDTO dto) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
    	if (restauranteOp.isEmpty()) {
    		throw new NotFoundException("Restaurante não existe");
    	}
		    	/*Código antes da implementação do DTO e Mapstruct"
		    	 *Prato prato = new Prato();
		    	 *prato.nome = dto.nome;
		    	 *prato.descricao = dto.descricao;
		    	 *prato.preco = dto.preco;
		    	 *prato.restaurante = restauranteOp.get();
		    	 *prato.persist();
		    	 *return Response.status(Status.CREATED).build();*/
    	
    	Prato prato = pratoMapper.toPrato(dto);
    	prato.restaurante = restauranteOp.get();
    	prato.persist();
    	return Response.status(Status.CREATED).build();
    	
    	
    }
    
    @PUT
    @Path("{id-restaurante}/pratos/{id}")
    @Transactional
    @Tag(name = "prato")
    public void atualizar(@PathParam("id-restaurante") Long idRestaurante, @PathParam("id") Long id, AtualizarPratoDTO dto) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
    	if(restauranteOp.isEmpty()) {
    		throw new NotFoundException("Restaurante não existe");
    	}
    	//No nosso caso, id do prato vai ser único, independente do restaurante...
    	Optional<Prato> pratoOp = Prato.findByIdOptional(id);
    	if(pratoOp.isEmpty()) {
    		throw new NotFoundException("Prato não existe");
    	}
    	Prato prato = pratoOp.get();
    	pratoMapper.toPrato(dto, prato);
    	prato.persist();
    }
    
    @DELETE
    @Path("{id-restaurante}/pratos/{id}")
    @Transactional
    @Tag(name = "prato")
    public void deletar(@PathParam("id-restaurante") Long idRestaurante, @PathParam("id") Long id) {
    	Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
    	if(restauranteOp.isEmpty()) {
    		throw new NotFoundException("Restaurante não existe");
    	}
    	//No nosso caso, id do prato vai ser único, independente do restaurante...
    	Optional<Prato> pratoOp = Prato.findByIdOptional(id);
    	pratoOp.ifPresentOrElse(Prato::delete, () -> {
    		throw new NotFoundException();
    	});
    }
}