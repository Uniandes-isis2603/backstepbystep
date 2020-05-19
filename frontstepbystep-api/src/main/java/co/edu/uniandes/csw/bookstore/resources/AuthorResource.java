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

import co.edu.uniandes.csw.bookstore.dtos.AuthorDTO;
import co.edu.uniandes.csw.bookstore.dtos.AuthorDetailDTO;
import co.edu.uniandes.csw.bookstore.ejb.AuthorLogic;
import co.edu.uniandes.csw.bookstore.entities.AuthorEntity;
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
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.WebApplicationException;

/**
 * Clase que implementa el recurso "authors".
 *
 * @author ISIS2603
 * @version 1.0
 */
@Path("/authors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AuthorResource {

    private static final Logger LOGGER = Logger.getLogger(AuthorResource.class.getName());

    @Inject
    private AuthorLogic authorLogic;

    /**
     * Crea un nuevo autor con la informacion que se recibe en el cuerpo de la
     * petición y se regresa un objeto identico con un id auto-generado por la
     * base de datos.
     *
     * @param author {@link AuthorDTO} - EL autor que se desea guardar.
     * @return JSON {@link AuthorDTO} - El autor guardado con el atributo id
     * autogenerado.
     */
    @POST
    public AuthorDTO createAuthor(AuthorDTO author) {
        LOGGER.log(Level.INFO, "AuthorResource createAuthor: input: {0}", author);
        AuthorDTO authorDTO = new AuthorDTO(authorLogic.createAuthor(author.toEntity()));
        LOGGER.log(Level.INFO, "AuthorResource createAuthor: output: {0}", authorDTO);
        return authorDTO;
    }
    
    /**
     * Crea un nuevo autor con la informacion que se recibe en el cuerpo de la
     * petición y se regresa un objeto identico con un id auto-generado por la
     * base de datos.
     *
     * @param authorD {@link AuthorDetailDTO} - El autor que se desea guardar.
     * @return JSON {@link AuthorDTO} - El autor guardado con el atributo id
     * autogenerado.
     */
    @POST
    @Path("{author: \\d+}")
    public AuthorDetailDTO createAuthorDetail(AuthorDetailDTO authorD) {
        LOGGER.log(Level.INFO, "AuthorDetailResource createAuthorDetail: input: {0}", authorD);
        AuthorDetailDTO authorDTO = new AuthorDetailDTO(authorLogic.createAuthor(authorD.toEntity()));
        LOGGER.log(Level.INFO, "AuthorDetailResource createAuthorDetail: output: {0}", authorDTO);
        return authorDTO;
    }

    /**
     * Busca y devuelve todos los autores que existen en la aplicacion.
     *
     * @return JSONArray {@link AuthorDetailDTO} - Los autores encontrados en la
     * aplicación. Si no hay ninguno retorna una lista vacía.
     */
    @GET
    public List<AuthorDetailDTO> getAuthors() {
        LOGGER.info("AuthorResource getAuthors: input: void");
        List<AuthorDetailDTO> listaAuthors = listEntity2DTO(authorLogic.getAuthors());
        LOGGER.log(Level.INFO, "AuthorResource getAuthors: output: {0}", listaAuthors);
        return listaAuthors;
    }

    /**
     * Busca el autor con el id asociado recibido en la URL y lo devuelve.
     *
     * @param authorsId Identificador del autor que se esta buscando. Este debe
     * ser una cadena de dígitos.
     * @return JSON {@link AuthorDetailDTO} - El autor buscado
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el autor.
     */
    @GET
    @Path("{authorsId: \\d+}")
    public AuthorDetailDTO getAuthor(@PathParam("authorsId") Long authorsId) {
        LOGGER.log(Level.INFO, "AuthorResource getAuthor: input: {0}", authorsId);
        AuthorEntity authorEntity = authorLogic.getAuthor(authorsId);
        if (authorEntity == null) {
            throw new WebApplicationException("El recurso /authors/" + authorsId + " no existe.", 404);
        }
        AuthorDetailDTO detailDTO = new AuthorDetailDTO(authorEntity);
        LOGGER.log(Level.INFO, "AuthorResource getAuthor: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Actualiza el autor con el id recibido en la URL con la información que se
     * recibe en el cuerpo de la petición.
     *
     * @param authorsId Identificador del autor que se desea actualizar. Este
     * debe ser una cadena de dígitos.
     * @param author {@link AuthorDetailDTO} El autor que se desea guardar.
     * @return JSON {@link AuthorDetailDTO} - El autor guardado.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el autor a
     * actualizar.
     */
    @PUT
    @Path("{authorsId: \\d+}")
    public AuthorDetailDTO updateAuthor(@PathParam("authorsId") Long authorsId, AuthorDetailDTO author) {
        LOGGER.log(Level.INFO, "AuthorResource updateAuthor: input: authorsId: {0} , author: {1}", new Object[]{authorsId, author});
        author.setId(authorsId);
        if (authorLogic.getAuthor(authorsId) == null) {
            throw new WebApplicationException("El recurso /authors/" + authorsId + " no existe.", 404);
        }
        AuthorDetailDTO detailDTO = new AuthorDetailDTO(authorLogic.updateAuthor(authorsId, author.toEntity()));
        LOGGER.log(Level.INFO, "AuthorResource updateAuthor: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Borra el autor con el id asociado recibido en la URL.
     *
     * @param authorsId Identificador del autor que se desea borrar. Este debe
     * ser una cadena de dígitos.
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     * si el autor tiene libros asociados
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error de lógica que se genera cuando no se encuentra el autor a borrar.
     */
    @DELETE
    @Path("{authorsId: \\d+}")
    public void deleteAuthor(@PathParam("authorsId") Long authorsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "AuthorResource deleteAuthor: input: {0}", authorsId);
        if (authorLogic.getAuthor(authorsId) == null) {
            throw new WebApplicationException("El recurso /authors/" + authorsId + " no existe.", 404);
        }
        authorLogic.deleteAuthor(authorsId);
        LOGGER.info("AuthorResource deleteAuthor: output: void");
    }

    /**
     * Conexión con el servicio de libros para un autor.
     * {@link AuthorBooksResource}
     *
     * Este método conecta la ruta de /authors con las rutas de /books que
     * dependen del autor, es una redirección al servicio que maneja el segmento
     * de la URL que se encarga de los libros.
     *
     * @param authorsId El ID del autor con respecto al cual se accede al
     * servicio.
     * @return El servicio de Libros para ese autor en paricular.
     */
    @Path("{authorsId: \\d+}/books")
    public Class<AuthorBooksResource> getAuthorBooksResource(@PathParam("authorsId") Long authorsId) {
        if (authorLogic.getAuthor(authorsId) == null) {
            throw new WebApplicationException("El recurso /authors/" + authorsId + " no existe.", 404);
        }
        return AuthorBooksResource.class;
    }

    /**
     * Convierte una lista de AuthorEntity a una lista de AuthorDetailDTO.
     *
     * @param entityList Lista de AuthorEntity a convertir.
     * @return Lista de AuthorDetailDTO convertida.
     */
    private List<AuthorDetailDTO> listEntity2DTO(List<AuthorEntity> entityList) {
        List<AuthorDetailDTO> list = new ArrayList<>();
        for (AuthorEntity entity : entityList) {
            list.add(new AuthorDetailDTO(entity));
        }
        return list;
    }
}
