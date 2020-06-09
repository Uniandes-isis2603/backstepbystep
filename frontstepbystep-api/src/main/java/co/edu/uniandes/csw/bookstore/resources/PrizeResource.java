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

import co.edu.uniandes.csw.bookstore.dtos.PrizeDTO;
import co.edu.uniandes.csw.bookstore.dtos.PrizeDetailDTO;
import co.edu.uniandes.csw.bookstore.ejb.OrganizationLogic;
import co.edu.uniandes.csw.bookstore.ejb.PrizeLogic;
import co.edu.uniandes.csw.bookstore.entities.PrizeEntity;
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.bookstore.mappers.BusinessLogicExceptionMapper;
import co.edu.uniandes.csw.bookstore.mappers.WebApplicationExceptionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

/**
 * Clase que implementa el recurso "prizes".
 *
 * @author ISIS2603
 * @version 1.0
 */
@Path("prizes")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class PrizeResource {

    private static final Logger LOGGER = Logger.getLogger(PrizeResource.class.getName());

    @Inject
    private PrizeLogic prizeLogic; // Variable para acceder a la lógica de la aplicación. Es una inyección de dependencias.

    @Inject
    private OrganizationLogic organizationLogic; // Variable para acceder a la lógica de la aplicación. Es una inyección de dependencias.

    private static final String MSG1 = "El recurso /prizes/";
    private static final String MSG2 = " no existe.";

    /**
     * Crea un nuevo premio con la informacion que se recibe en el cuerpo de la
     * petición y se regresa un objeto identico con un id auto-generado por la
     * base de datos.
     *
     * @param prize {@link PrizeDTO} - EL premio que se desea guardar.
     * @return JSON {@link PrizeDTO} - El premio guardado con el atributo id
     * autogenerado.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando ya existe el premio o el isbn es
     * inválido o si la organization ingresada es invalida.
     */
    @POST
    public PrizeDTO createPrize(PrizeDTO prize) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "PrizeResource createPrize: input: {0}", prize);
        PrizeDTO nuevoPrizeDTO = new PrizeDTO(prizeLogic.createPrize(prize.toEntity()));
        LOGGER.log(Level.INFO, "PrizeResource createPrize: output: {0}", nuevoPrizeDTO);
        return nuevoPrizeDTO;
    }

    /**
     * Busca y devuelve todos los premios que existen en la aplicacion.
     *
     * @return JSONArray {@link PrizeDetailDTO} - Los premios encontrados en la
     * aplicación. Si no hay ninguno retorna una lista vacía.
     */
    @GET
    public List<PrizeDetailDTO> getPrizes() {
        LOGGER.info("PrizeResource getPrizes: input: void");
        List<PrizeDetailDTO> listaPrizes = listEntity2DetailDTO(prizeLogic.getPrizes());
        LOGGER.log(Level.INFO, "PrizeResource getPrizes: output: {0}", listaPrizes);
        return listaPrizes;
    }

    /**
     * Busca el premio con el id asociado recibido en la URL y lo devuelve.
     *
     * @param prizesId Identificador del premio que se esta buscando. Este debe
     * ser una cadena de dígitos.
     * @return JSON {@link PrizeDetailDTO} - El premio buscado
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el premio.
     */
    @GET
    @Path("{prizesId: \\d+}")
    public PrizeDetailDTO getPrize(@PathParam("prizesId") Long prizesId) {
        LOGGER.log(Level.INFO, "PrizeResource getPrize: input: {0}", prizesId);
        PrizeEntity prizeEntity = prizeLogic.getPrize(prizesId);
        if (prizeEntity == null) {
            throw new WebApplicationException(MSG1 + prizesId + MSG2, 404);
        }
        PrizeDetailDTO prizeDetailDTO = new PrizeDetailDTO(prizeEntity);
        LOGGER.log(Level.INFO, "PrizeResource getPrize: output: {0}", prizeDetailDTO);
        return prizeDetailDTO;
    }

    /**
     * Actualiza el premio con el id recibido en la URL con la información que
     * se recibe en el cuerpo de la petición.
     *
     * @param prizesId Identificador del premio que se desea actualizar. Este
     * debe ser una cadena de dígitos.
     * @param prize {@link PrizeDTO} El premio que se desea guardar.
     * @return JSON {@link PrizeDetailDTO} - El premio guardada.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra el premio a
     * actualizar.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando no se puede actualizar el premio.
     */
    @PUT
    @Path("{prizesId: \\d+}")
    public PrizeDetailDTO updatePrize(@PathParam("prizesId") Long prizesId, PrizeDTO prize) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "PrizeResource updatePrize: input: prizesId: {0} , prize: {1}", new Object[]{prizesId, prize});
        prize.setId(prizesId);
        if (prizeLogic.getPrize(prizesId) == null) {
            throw new WebApplicationException(MSG1 + prizesId + MSG2, 404);
        }
        PrizeDetailDTO detailDTO = new PrizeDetailDTO(prizeLogic.updatePrize(prizesId, prize.toEntity()));
        LOGGER.log(Level.INFO, "PrizeResource updatePrize: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Borra el premio con el id asociado recibido en la URL.
     *
     * @param prizesId Identificador del premio que se desea borrar. Este debe
     * ser una cadena de dígitos.
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     * Error de lógica que se genera cuando el premio tiene un autor.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper}
     * Error de lógica que se genera cuando no se encuentra el premio.
     */
    @DELETE
    @Path("{prizesId: \\d+}")
    public void deletePrize(@PathParam("prizesId") Long prizesId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "PrizeResource deletePrize: input: {0}", prizesId);
        if (prizeLogic.getPrize(prizesId) == null) {
            throw new WebApplicationException(MSG1 + prizesId + MSG2, 404);
        }
        prizeLogic.deletePrize(prizesId);
        LOGGER.info("PrizeResource deletePrize: output: void");
    }

    /**
     * Conexión con el servicio de libros para una editorial.
     * {@link PrizeAuthorResource}
     *
     * Este método conecta la ruta de /prizes con las rutas de /author que
     * dependen del premio, es una redirección al servicio que maneja el
     * segmento de la URL que se encarga del autor del premio.
     *
     * @param prizesId El ID de la editorial con respecto a la cual se accede al
     * servicio.
     * @return El servicio de autor para este premio en paricular.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se el premio.
     */
    @Path("{prizesId: \\d+}/author")
    public Class<PrizeAuthorResource> getPrizeAuthorResource(@PathParam("prizesId") Long prizesId) {
        if (prizeLogic.getPrize(prizesId) == null) {
            throw new WebApplicationException(MSG1 + prizesId + MSG2, 404);
        }
        return PrizeAuthorResource.class;
    }

    /**
     * Convierte una lista de entidades a DTO.
     *
     * Este método convierte una lista de objetos PrizeEntity a una lista de
     * objetos PrizeDetailDTO (json)
     *
     * @param entityList corresponde a la lista de premios de tipo Entity que
     * vamos a convertir a DTO.
     * @return la lista de premios en forma DTO (json)
     */
    private List<PrizeDetailDTO> listEntity2DetailDTO(List<PrizeEntity> entityList) {
        List<PrizeDetailDTO> list = new ArrayList<>();
        for (PrizeEntity entity : entityList) {
            list.add(new PrizeDetailDTO(entity));
        }
        return list;
    }
}
