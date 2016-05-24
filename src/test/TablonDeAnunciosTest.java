package test;

import static org.junit.Assert.*;
import org.junit.Before;
import junio2013.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class TablonDeAnunciosTest {
	TablonDeAnuncios tablon_;
	Anuncio anuncio_;
	IBaseDeDatosDeAnunciantes bdAnunciantes;
	IBaseDeDatosDePagos bdPagos;

	@Before
	public void setUp() {
		tablon_ = new TablonDeAnuncios();
		bdAnunciantes = mock(IBaseDeDatosDeAnunciantes.class);
		bdPagos = mock(IBaseDeDatosDePagos.class);
	}

	@Test
	public void inicialmenteHayUnAnuncioEnElTablon() {
		assertEquals(1, tablon_.anunciosPublicados());
	}

	@Test
	public void creoAnuncioDeLAEMPRESAyComprueboQueSeHaInsertadoCorrectamente() {
		anuncio_ = new Anuncio("Titulo", "Cuerpo del mensaje", "LA EMPRESA");
		tablon_.publicarAnuncio(anuncio_, null, null);

		assertNotEquals("La busqueda del anuncio de la empresa devuelve NULL", null,
				tablon_.buscarAnuncioPorTitulo("Titulo"));
		assertEquals("El numero de anuncios publicados es distinto de 2", 2, tablon_.anunciosPublicados());
	}

	@Test
	public void publicarAnuncioDeAnuncianteDistintoALAEMPRESAsinSaldo() {
		anuncio_ = new Anuncio("Renault Megane", "Se vende renault megane de 2008", "MIL ANUNCIOS");

		when(bdAnunciantes.buscarAnunciante("MIL ANUNCIOS")).thenReturn(true);
		when(bdPagos.anuncianteTieneSaldo("MIL ANUNCIOS")).thenReturn(false);

		int numAnunciosAntesDePublicar = tablon_.anunciosPublicados();
		tablon_.publicarAnuncio(anuncio_, bdAnunciantes, bdPagos);

		assertEquals("El anuncio se ha publicado, aunque no debia porque el anunciante no tiene saldo",
				numAnunciosAntesDePublicar, tablon_.anunciosPublicados());
		assertEquals("La busqueda del anuncio que se ha intentado publicar no ha devuleto null", null,
				tablon_.buscarAnuncioPorTitulo("Renault Megane"));

		verify(bdAnunciantes).buscarAnunciante("MIL ANUNCIOS");
		verify(bdPagos).anuncianteTieneSaldo("MIL ANUNCIOS");
	}

	@Test
	public void publicarAnuncioDeAnuncianteDistintoALAEMPRESAconSaldo() {
		anuncio_ = new Anuncio("Ordenador ACER", "Vendo ordenador marca ACER", "MIL ANUNCIOS");

		when(bdAnunciantes.buscarAnunciante("MIL ANUNCIOS")).thenReturn(true);
		when(bdPagos.anuncianteTieneSaldo("MIL ANUNCIOS")).thenReturn(true);

		int numAnunciosAntesDePublicar = tablon_.anunciosPublicados();
		tablon_.publicarAnuncio(anuncio_, bdAnunciantes, bdPagos);

		assertNotEquals("El anuncio se ha publicado", numAnunciosAntesDePublicar, tablon_.anunciosPublicados());
		assertEquals(anuncio_, tablon_.buscarAnuncioPorTitulo("Ordenador ACER"));

		verify(bdAnunciantes).buscarAnunciante("MIL ANUNCIOS");
		verify(bdPagos).anuncianteTieneSaldo("MIL ANUNCIOS");
		verify(bdPagos).anuncioPublicado("MIL ANUNCIOS");
	}

	@Test
	public void publicarDosAnunciosDeLAEMPRESAbuscarElSegundoPorTituloYComprobarQueElTamannoDelTablonNoAumenta() {

		anuncio_ = new Anuncio("Titulo", "Cuerpo del mensaje", "LA EMPRESA");
		tablon_.publicarAnuncio(anuncio_, null, null);
		int numAnunciosDespuesDelPrimero = tablon_.anunciosPublicados();

		anuncio_ = new Anuncio("Titulo2", "Cuerpo del mensaje2", "LA EMPRESA");
		tablon_.publicarAnuncio(anuncio_, null, null);

		assertEquals("El ultimo anuncio publicado no es correcto", anuncio_, tablon_.buscarAnuncioPorTitulo("Titulo2"));
		assertEquals("El numero de anuncios a aumentado, pero no debia hacerlo", numAnunciosDespuesDelPrimero,
				numAnunciosDespuesDelPrimero);
	}

	@Test
	public void comprobarQueAlPublicarDosAnunciosDelMismoAnuncianteElPrimeroSeBorraCorrectamente() {
		Anuncio primerAnuncio = new Anuncio("Titulo", "Cuerpo del mensaje", "LA EMPRESA");
		tablon_.publicarAnuncio(primerAnuncio, bdAnunciantes, bdPagos);

		anuncio_ = new Anuncio("Titulo2", "Cuerpo del mensaje2", "LA EMPRESA");
		tablon_.publicarAnuncio(anuncio_, null, null);

		tablon_.borrarAnuncio("Titulo", "LA EMPRESA");

		assertNotEquals("El anuncio no se ha borrado", primerAnuncio, (tablon_.buscarAnuncioPorTitulo("Titulo")));
	}

	@Test
	public void noSeDebePoderPublicarMasDeUnAnuncioConElMismoTituloYDelMismoAnunciante() {
		when(bdAnunciantes.buscarAnunciante("MIL ANUNCIOS")).thenReturn(true);
		when(bdPagos.anuncianteTieneSaldo("MIL ANUNCIOS")).thenReturn(true);

		anuncio_ = new Anuncio("Titulo", "Cuerpo del mensaje", "MIL ANUNCIOS");
		tablon_.publicarAnuncio(anuncio_, bdAnunciantes, bdPagos);
		tablon_.publicarAnuncio(anuncio_, bdAnunciantes, bdPagos);

		verify(bdAnunciantes, times(2)).buscarAnunciante("MIL ANUNCIOS");
		verify(bdPagos, times(2)).anuncianteTieneSaldo("MIL ANUNCIOS");
		verify(bdPagos).anuncioPublicado("MIL ANUNCIOS");
	}
}
