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

import co.edu.uniandes.csw.bookstore.dtos.ReviewDTO;
import co.edu.uniandes.csw.bookstore.ejb.ReviewLogic;
import co.edu.uniandes.csw.bookstore.entities.ReviewEntity;
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.bookstore.mappers.BusinessLogicExceptionMapper;
import co.edu.uniandes.csw.bookstore.mappers.WebApplicationExceptionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

/**
 * Clase que implementa el recurso "reviews".
 *
 * @author ISIS2603
 * @version 1.0
 */
@Produces("application/json")
@Consumes("application/json")
public class ReviewResource {

    private static final Logger LOGGER = Logger.getLogger(ReviewResource.class.getName());

    @Inject
    private ReviewLogic reviewLogic;
    
    private static final String MSG1 = "El recurso /books/";
    private static final String MSG2 = "/reviews/"; 
    private static final String MSG3 = " no existe."; 

    /**
     * Crea una nueva reseña con la informacion que se recibe en el cuerpo de la
     * petición y se regresa un objeto identico con un id auto-generado por la
     * base de datos.
     *
     * @param booksId El ID del libro del cual se le agrega la reseña
     * @param review {@link ReviewDTO} - La reseña que se desea guardar.
     * @return JSON {@link ReviewDTO} - La reseña guardada con el atributo id
     * autogenerado.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando ya existe la reseña.
     */
    @POST
    public ReviewDTO createReview(@PathParam("booksId") Long booksId, ReviewDTO review) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "ReviewResource createReview: input: {0}", review);
        ReviewDTO nuevoReviewDTO = new ReviewDTO(reviewLogic.createReview(booksId, review.toEntity()));
        LOGGER.log(Level.INFO, "ReviewResource createReview: output: {0}", nuevoReviewDTO);
        return nuevoReviewDTO;
    }

    /**
     * Busca y devuelve todas las reseñas que existen en un libro.
     *
     * @param booksId El ID del libro del cual se buscan las reseñas
     * @return JSONArray {@link ReviewDTO} - Las reseñas encontradas en el
     * libro. Si no hay ninguna retorna una lista vacía.
     */
    @GET
    public List<ReviewDTO> getReviews(@PathParam("booksId") Long booksId) {
        LOGGER.log(Level.INFO, "ReviewResource getReviews: input: {0}", booksId);
        List<ReviewDTO> listaDTOs = listEntity2DTO(reviewLogic.getReviews(booksId));
        LOGGER.log(Level.INFO, "EditorialBooksResource getBooks: output: {0}", listaDTOs);
        return listaDTOs;
    }

    /**
     * Busca y devuelve la reseña con el ID recibido en la URL, relativa a un
     * libro.
     *
     * @param booksId El ID del libro del cual se buscan las reseñas
     * @param reviewsId El ID de la reseña que se busca
     * @return {@link ReviewDTO} - La reseña encontradas en el libro.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el libro.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra la reseña.
     */
    @GET
    @Path("{reviewsId: \\d+}")
    public ReviewDTO getReview(@PathParam("booksId") Long booksId, @PathParam("reviewsId") Long reviewsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "ReviewResource getReview: input: {0}", reviewsId);
        ReviewEntity entity = reviewLogic.getReview(booksId, reviewsId);
        if (entity == null) {
            throw new WebApplicationException(MSG1 + booksId + MSG2 + reviewsId + MSG3, 404);
        }
        ReviewDTO reviewDTO = new ReviewDTO(entity);
        LOGGER.log(Level.INFO, "ReviewResource getReview: output: {0}", reviewDTO);
        return reviewDTO;
    }

    /**
     * Actualiza una reseña con la informacion que se recibe en el cuerpo de la
     * petición y se regresa el objeto actualizado.
     *
     * @param booksId El ID del libro del cual se guarda la reseña
     * @param reviewsId El ID de la reseña que se va a actualizar
     * @param review {@link ReviewDTO} - La reseña que se desea guardar.
     * @return JSON {@link ReviewDTO} - La reseña actualizada.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando ya existe la reseña.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra la reseña.
     */
    @PUT
    @Path("{reviewsId: \\d+}")
    public ReviewDTO updateReview(@PathParam("booksId") Long booksId, @PathParam("reviewsId") Long reviewsId, ReviewDTO review) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "ReviewResource updateReview: input: booksId: {0} , reviewsId: {1} , review:{2}", new Object[]{booksId, reviewsId, review});
        if (!reviewsId.equals(review.getId())) {
            throw new BusinessLogicException("Los ids del Review no coinciden.");
        }
        ReviewEntity entity = reviewLogic.getReview(booksId, reviewsId);
        if (entity == null) {
            throw new WebApplicationException(MSG1 + booksId + MSG2 + reviewsId + MSG3, 404);

        }
        ReviewDTO reviewDTO = new ReviewDTO(reviewLogic.updateReview(booksId, review.toEntity()));
        LOGGER.log(Level.INFO, "ReviewResource updateReview: output:{0}", reviewDTO);
        return reviewDTO;

    }

    /**
     * Borra la reseña con el id asociado recibido en la URL.
     *
     * @param booksId El ID del libro del cual se va a eliminar la reseña.
     * @param reviewsId El ID de la reseña que se va a eliminar.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando no se puede eliminar la reseña.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra la reseña.
     */
    @DELETE
    @Path("{reviewsId: \\d+}")
    public void deleteReview(@PathParam("booksId") Long booksId, @PathParam("reviewsId") Long reviewsId) throws BusinessLogicException {
        ReviewEntity entity = reviewLogic.getReview(booksId, reviewsId);
        if (entity == null) {
            throw new WebApplicationException(MSG1 + booksId + MSG2 + reviewsId + MSG3, 404);
        }
        reviewLogic.deleteReview(booksId, reviewsId);
    }

    /**
     * Lista de entidades a DTO.
     *
     * Este método convierte una lista de objetos PrizeEntity a una lista de
     * objetos ReviewDTO (json)
     *
     * @param entityList corresponde a la lista de reseñas de tipo Entity que
     * vamos a convertir a DTO.
     * @return la lista de reseñas en forma DTO (json)
     */
    private List<ReviewDTO> listEntity2DTO(List<ReviewEntity> entityList) {
        List<ReviewDTO> list = new ArrayList<>();
        for (ReviewEntity entity : entityList) {
            list.add(new ReviewDTO(entity));
        }
        return list;
    }
}
