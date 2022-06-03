package com.github.matheusvsoliveira.ifood.mp;


import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;

public class RestauranteCadastrado {

	@Inject
	PgPool pgPool;

	@Incoming("restaurantes")
	@Blocking
	public void receberRestaurante(JsonObject json) {
		Restaurante restaurante = json.mapTo(Restaurante.class);
		System.out.println("------------------------------");
		System.out.println(json);
		System.out.println("------------------------------");
		System.out.println(restaurante);
		restaurante.persist(pgPool);
	}
}