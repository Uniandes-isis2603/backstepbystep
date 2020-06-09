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

import co.edu.uniandes.csw.bookstore.dtos.AuthorDetailDTO;
import co.edu.uniandes.csw.bookstore.ejb.AuthorLogic;
import co.edu.uniandes.csw.bookstore.ejb.BookAuthorLogic;
import co.edu.uniandes.csw.bookstore.entities.AuthorEntity;
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
 * Clase que implementa el recurso "books/{id}/authors".
 *
 * @author ISIS2603
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookAuthorsResource {

    private static final Logger LOGGER = Logger.getLogger(BookAuthorsResource.class.getName());

    @Inject
    private BookAuthorLogic bookAuthorLogic;

    @Inject
    private AuthorLogic authorLogic;

    private static final String MSG1 = "El recurso /authors/";
    private static final String MSG2 = " no existe.";

    /**
     * Asocia un autor existente con un libro existente
     *
     * @param authorsId El ID del autor que se va a asociar
     * @param booksId El ID del libro al cual se le va a asociar el autor
     * @return JSON {@link AuthorDetailDTO} - El autor asociado.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el autor.
     */
    @POST
    @Path("{authorsId: \\d+}")
    public AuthorDetailDTO addAuthor(@PathParam("booksId") Long booksId, @PathParam("authorsId") Long authorsId) {
        LOGGER.log(Level.INFO, "BookAuthorsResource addAuthor: input: booksId {0} , authorsId {1}", new Object[]{booksId, authorsId});
        if (authorLogic.getAuthor(authorsId) == null) {
            throw new WebApplicationException(MSG1 + authorsId + MSG2, 404);
        }
        AuthorDetailDTO detailDTO = new AuthorDetailDTO(bookAuthorLogic.addAuthor(booksId, authorsId));
        LOGGER.log(Level.INFO, "BookAuthorsResource addAuthor: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Busca y devuelve todos los autores que existen en un libro.
     *
     * @param booksId El ID del libro del cual se buscan los autores
     * @return JSONArray {@link AuthorDetailDTO} - Los autores encontrados en el
     * libro. Si no hay ninguno retorna una lista vacía.
     */
    @GET
    public List<AuthorDetailDTO> getAuthors(@PathParam("booksId") Long booksId) {
        LOGGER.log(Level.INFO, "BookAuthorsResource getAuthors: input: {0}", booksId);
        List<AuthorDetailDTO> lista = authorsListEntity2DTO(bookAuthorLogic.getAuthors(booksId));
        LOGGER.log(Level.INFO, "BookAuthorsResource getAuthors: output: {0}", lista);
        return lista;
    }

    /**
     * Busca y devuelve el autor con el ID recibido en la URL, relativo a un
     * libro.
     *
     * @param authorsId El ID del autor que se busca
     * @param booksId El ID del libro del cual se busca el autor
     * @return {@link AuthorDetailDTO} - El autor encontrado en el libro.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error de lógica que se genera cuando no se encuentra el autor.
     */
    @GET
    @Path("{authorsId: \\d+}")
    public AuthorDetailDTO getAuthor(@PathParam("booksId") Long booksId, @PathParam("authorsId") Long authorsId) {
        LOGGER.log(Level.INFO, "BookAuthorsResource getAuthor: input: booksId {0} , authorsId {1}", new Object[]{booksId, authorsId});
        if (authorLogic.getAuthor(authorsId) == null) {
            throw new WebApplicationException(MSG1 + authorsId + MSG2, 404);
        }
        AuthorDetailDTO detailDTO = new AuthorDetailDTO(bookAuthorLogic.getAuthor(booksId, authorsId));
        LOGGER.log(Level.INFO, "BookAuthorsResource getAuthor: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Actualiza la lista de autores de un libro con la lista que se recibe en
     * el cuerpo.
     *
     * @param booksId El ID del libro al cual se le va a asociar la lista de
     * autores
     * @param authors JSONArray {@link AuthorDetailDTO} - La lista de autores
     * que se desea guardar.
     * @return JSONArray {@link AuthorDetailDTO} - La lista actualizada.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error de lógica que se genera cuando no se encuentra el autor.
     */
    @PUT
    public List<AuthorDetailDTO> replaceAuthors(@PathParam("booksId") Long booksId, List<AuthorDetailDTO> authors) {
        LOGGER.log(Level.INFO, "BookAuthorsResource replaceAuthors: input: booksId {0} , authors {1}", new Object[]{booksId, authors});
        for (AuthorDetailDTO author : authors) {
            if (authorLogic.getAuthor(author.getId()) == null) {
                throw new WebApplicationException(MSG1 + author.getId() + MSG2, 404);
            }
        }
        List<AuthorDetailDTO> lista = authorsListEntity2DTO(bookAuthorLogic.replaceAuthors(booksId, authorsListDTO2Entity(authors)));
        LOGGER.log(Level.INFO, "BookAuthorsResource replaceAuthors: output:{0}", lista);
        return lista;
    }

    /**
     * Elimina la conexión entre el autor y el libro recibidos en la URL.
     *
     * @param booksId El ID del libro al cual se le va a desasociar el autor
     * @param authorsId El ID del autor que se desasocia
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error de lógica que se genera cuando no se encuentra el autor.
     */
    @DELETE
    @Path("{authorsId: \\d+}")
    public void removeAuthor(@PathParam("booksId") Long booksId, @PathParam("authorsId") Long authorsId) {
        LOGGER.log(Level.INFO, "BookAuthorsResource removeAuthor: input: booksId {0} , authorsId {1}", new Object[]{booksId, authorsId});
        if (authorLogic.getAuthor(authorsId) == null) {
            throw new WebApplicationException(MSG1 + authorsId + MSG2, 404);
        }
        bookAuthorLogic.removeAuthor(booksId, authorsId);
        LOGGER.info("BookAuthorsResource removeAuthor: output: void");
    }

    /**
     * Convierte una lista de AuthorEntity a una lista de AuthorDetailDTO.
     *
     * @param entityList Lista de AuthorEntity a convertir.
     * @return Lista de AuthorDetailDTO convertida.
     */
    private List<AuthorDetailDTO> authorsListEntity2DTO(List<AuthorEntity> entityList) {
        List<AuthorDetailDTO> list = new ArrayList<>();
        for (AuthorEntity entity : entityList) {
            list.add(new AuthorDetailDTO(entity));
        }
        return list;
    }

    /**
     * Convierte una lista de AuthorDetailDTO a una lista de AuthorEntity.
     *
     * @param dtos Lista de AuthorDetailDTO a convertir.
     * @return Lista de AuthorEntity convertida.
     */
    private List<AuthorEntity> authorsListDTO2Entity(List<AuthorDetailDTO> dtos) {
        List<AuthorEntity> list = new ArrayList<>();
        for (AuthorDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }
}
