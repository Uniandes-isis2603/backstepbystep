/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.bookstore.resources;

import co.edu.uniandes.csw.bookstore.dtos.BookDetailDTO;
import co.edu.uniandes.csw.bookstore.ejb.AuthorBooksLogic;
import co.edu.uniandes.csw.bookstore.ejb.BookLogic;
import co.edu.uniandes.csw.bookstore.entities.BookEntity;
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.bookstore.mappers.WebApplicationExceptionMapper;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

/**
 * Clase que implementa el recurso "authors/{id}/books".
 *
 * @author ISIS2603
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthorBooksResource {

    private static final Logger LOGGER = Logger.getLogger(AuthorBooksResource.class.getName());

    @Inject
    private AuthorBooksLogic authorBookLogic;

    @Inject
    private BookLogic bookLogic; // Variable para acceder a la lógica de la aplicación. Es una inyección de dependencias.

    private static final String MSG1 = "El recurso /books/";
    private static final String MSG2 = " no existe.";

    /**
     * Asocia un libro existente con un autor existente
     *
     * @param authorsId El ID del autor al cual se le va a asociar el libro
     * @param booksId El ID del libro que se asocia
     * @return JSON {@link BookDetailDTO} - El libro asociado.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el libro.
     */
    @POST
    @Path("{booksId: \\d+}")
    public BookDetailDTO addBook(@PathParam("authorsId") Long authorsId, @PathParam("booksId") Long booksId) {
        LOGGER.log(Level.INFO, "AuthorBooksResource addBook: input: authorsId {0} , booksId {1}", new Object[]{authorsId, booksId});
        if (bookLogic.getBook(booksId) == null) {
            throw new WebApplicationException(MSG1 + booksId + MSG2, 404);
        }
        BookDetailDTO detailDTO = new BookDetailDTO(authorBookLogic.addBook(authorsId, booksId));
        LOGGER.log(Level.INFO, "AuthorBooksResource addBook: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Busca y devuelve todos los libros que existen en un autor.
     *
     * @param authorsId El ID del autor del cual se buscan los libros
     * @return JSONArray {@link BookDetailDTO} - Los libros encontrados en el
     * autor. Si no hay ninguno retorna una lista vacía.
     */
    @GET
    public List<BookDetailDTO> getBooks(@PathParam("authorsId") Long authorsId) {
        LOGGER.log(Level.INFO, "AuthorBooksResource getBooks: input: {0}", authorsId);
        List<BookDetailDTO> lista = booksListEntity2DTO(authorBookLogic.getBooks(authorsId));
        LOGGER.log(Level.INFO, "AuthorBooksResource getBooks: output: {0}", lista);
        return lista;
    }

    /**
     * Busca y devuelve el libro con el ID recibido en la URL, relativo a un
     * autor.
     *
     * @param authorsId El ID del autor del cual se busca el libro
     * @param booksId El ID del libro que se busca
     * @return {@link BookDetailDTO} - El libro encontrado en el autor.
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     * si el libro no está asociado al autor
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el libro.
     */
    @GET
    @Path("{booksId: \\d+}")
    public BookDetailDTO getBook(@PathParam("authorsId") Long authorsId, @PathParam("booksId") Long booksId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "AuthorBooksResource getBook: input: authorsId {0} , booksId {1}", new Object[]{authorsId, booksId});
        if (bookLogic.getBook(booksId) == null) {
            throw new WebApplicationException(MSG1 + booksId + MSG2, 404);
        }
        BookDetailDTO detailDTO = new BookDetailDTO(authorBookLogic.getBook(authorsId, booksId));
        LOGGER.log(Level.INFO, "AuthorBooksResource getBook: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Actualiza la lista de libros de un autor con la lista que se recibe en el
     * cuerpo
     *
     * @param authorsId El ID del autor al cual se le va a asociar el libro
     * @param books JSONArray {@link BookDetailDTO} - La lista de libros que se
     * desea guardar.
     * @return JSONArray {@link BookDetailDTO} - La lista actualizada.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el libro.
     */
    @PUT
    public List<BookDetailDTO> replaceBooks(@PathParam("authorsId") Long authorsId, List<BookDetailDTO> books) {
        LOGGER.log(Level.INFO, "AuthorBooksResource replaceBooks: input: authorsId {0} , books {1}", new Object[]{authorsId, books});
        for (BookDetailDTO book : books) {
            if (bookLogic.getBook(book.getId()) == null) {
                throw new WebApplicationException(MSG1 + book.getId() + MSG2, 404);
            }
        }
        List<BookDetailDTO> lista = booksListEntity2DTO(authorBookLogic.replaceBooks(authorsId, booksListDTO2Entity(books)));
        LOGGER.log(Level.INFO, "AuthorBooksResource replaceBooks: output: {0}", lista);
        return lista;
    }

    /**
     * Elimina la conexión entre el libro y e autor recibidos en la URL.
     *
     * @param authorsId El ID del autor al cual se le va a desasociar el libro
     * @param booksId El ID del libro que se desasocia
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el libro.
     */
    @DELETE
    @Path("{booksId: \\d+}")
    public void removeBook(@PathParam("authorsId") Long authorsId, @PathParam("booksId") Long booksId) {
        LOGGER.log(Level.INFO, "AuthorBooksResource deleteBook: input: authorsId {0} , booksId {1}", new Object[]{authorsId, booksId});
        if (bookLogic.getBook(booksId) == null) {
            throw new WebApplicationException(MSG1 + booksId + MSG2, 404);
        }
        authorBookLogic.removeBook(authorsId, booksId);
        LOGGER.info("AuthorBooksResource deleteBook: output: void");
    }

    /**
     * Convierte una lista de BookEntity a una lista de BookDetailDTO.
     *
     * @param entityList Lista de BookEntity a convertir.
     * @return Lista de BookDetailDTO convertida.
     */
    private List<BookDetailDTO> booksListEntity2DTO(List<BookEntity> entityList) {
        List<BookDetailDTO> list = new ArrayList<>();
        for (BookEntity entity : entityList) {
            list.add(new BookDetailDTO(entity));
        }
        return list;
    }

    /**
     * Convierte una lista de BookDetailDTO a una lista de BookEntity.
     *
     * @param dtos Lista de BookDetailDTO a convertir.
     * @return Lista de BookEntity convertida.
     */
    private List<BookEntity> booksListDTO2Entity(List<BookDetailDTO> dtos) {
        List<BookEntity> list = new ArrayList<>();
        for (BookDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
