package junio2013;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: anton_000 Date: 11/06/13 Time: 8:44 To
 * change this template use File | Settings | File Templates.
 */
public class TablonDeAnuncios {
	private List<Anuncio> listaDeAnuncios_;

	/**
	 * Constructor
	 */
	public TablonDeAnuncios() {
		listaDeAnuncios_ = new ArrayList<Anuncio>();
		Anuncio anuncio = new Anuncio("Bienvenidos", "Tablon para poner anuncios", "LA EMPRESA");
		listaDeAnuncios_.add(anuncio);
	}

	/**
	 * Devuelve el numero de anuncios publicados
	 * 
	 * @return numero de anuncios publicados
	 */
	public int anunciosPublicados() {
		return listaDeAnuncios_.size();
	}

	/**
	 * Publica un anuncio. Si el anunciante es "LA EMPRESA", se asume que es la
	 * propietaria del tablon de anuncios, por lo que puede publicar gratis sin
	 * restricciones.
	 * 
	 * @param anuncio
	 *            El anuncio
	 * @param bdAnunciantes
	 *            Base de datos de anunciantes
	 * @param bdPagos
	 *            Base de datos de pagos.
	 */
	public void publicarAnuncio(Anuncio anuncio, IBaseDeDatosDeAnunciantes bdAnunciantes, IBaseDeDatosDePagos bdPagos) {
		if (anuncio.anunciante_.equals("LA EMPRESA"))
			listaDeAnuncios_.add(anuncio);
		else {
			if ((bdAnunciantes.buscarAnunciante(anuncio.anunciante_))
					&& (bdPagos.anuncianteTieneSaldo(anuncio.anunciante_))) {
				Anuncio esteAnuncio = buscarAnuncioPorTitulo(anuncio.titulo_);
				if (esteAnuncio == null || !(esteAnuncio.anunciante_.equals(anuncio.anunciante_))) {
					listaDeAnuncios_.add(anuncio);
					bdPagos.anuncioPublicado(anuncio.anunciante_);
				}
			}
		}
	}

	/**
	 * Buscar un anuncio por el titulo
	 *
	 * @param titulo
	 *            titulo del anuncio
	 * @return el anuncio si se encuentra, null en caso contrario
	 */
	public Anuncio buscarAnuncioPorTitulo(String titulo) {
		Anuncio resultado = null;
		for (Anuncio anuncio : listaDeAnuncios_) {
			if (anuncio.titulo_.equals(titulo))
				resultado = anuncio;
		}
		return resultado;
	}

	/**
	 * Borra un anuncio que se ajuste al titulo y anunciante que se pasan como
	 * argumentos
	 * 
	 * @param titulo
	 *            titulo del anuncio
	 * @param anunciante
	 *            nombre del anunciante
	 */
	public void borrarAnuncio(String titulo, String anunciante) {
		for (Anuncio anuncio : listaDeAnuncios_) {
			if ((anuncio.titulo_.equals(titulo) && (anuncio.anunciante_.equals(anunciante))))
				listaDeAnuncios_.remove(anuncio);
		}

	}
}
