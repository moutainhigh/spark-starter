package sample.doc.restdoc.controller;

import info.spark.starter.basic.Result;
import info.spark.starter.rest.base.AbstractController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import sample.doc.restdoc.entity.Pet;
import sample.doc.restdoc.entity.Pets;
import sample.doc.restdoc.repository.MapBackedRepository;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 22:17
 * @since 1.0.0
 */
@RestController
@RequestMapping("/pets")
@Api(tags = "Pets API", description = "Pets相关API")
public class PetController extends AbstractController {
    /** Pet data */
    private final PetRepository petData = new PetRepository();

    /**
     * Gets pet by id *
     *
     * @param petId pet id
     * @return the pet by id
     * @since 1.0.0
     */
    @GetMapping("{petId}")
    @ApiOperation(value = "通过ID查找Pet", notes = "", response = Pet.class,
                  authorizations = {
                      @Authorization(value = "api_key"),
                      @Authorization(value = "petstore_auth", scopes = {
                          @AuthorizationScope(scope = "write_pets", description = ""),
                          @AuthorizationScope(scope = "read_pets", description = "")
                      })})
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "非法ID值"),
        @ApiResponse(code = 404, message = "Pet not found")}
    )
    public Result<Pet> getPetById(
        @ApiParam(value = "需要查找的PetID", allowableValues = "range[1,5]", required = true)
        @PathVariable String petId) {
        return this.ok(this.petData.get(Long.valueOf(petId)));
    }

    /**
     * Add pet response entity
     *
     * @param pet pet
     * @return the response entity
     * @since 1.0.0
     */
    @PostMapping
    @ApiOperation("新增Pet")
    @ApiResponses(value = {@ApiResponse(code = 405, message = "非法输入")})
    public Result<String> addPet(@RequestBody Pet pet) {
        this.petData.add(pet);
        return this.ok();
    }

    /**
     * Update pet response entity
     *
     * @param pet pet
     * @return the response entity
     * @since 1.0.0
     */
    @PutMapping
    @ApiOperation(value = "更新pet",
                  authorizations = @Authorization(value = "petstore_auth", scopes = {
                      @AuthorizationScope(scope = "write_pets", description = ""),
                      @AuthorizationScope(scope = "read_pets", description = "")
                  }))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "非法ID值"),
                           @ApiResponse(code = 404, message = "Pet not found"),
                           @ApiResponse(code = 405, message = "Validation exception")})
    public Result<String> updatePet(@RequestBody Pet pet) {
        this.petData.add(pet);
        return this.ok();
    }

    /**
     * Find pets by status response entity
     *
     * @param status status
     * @return the response entity
     * @since 1.0.0
     */
    @GetMapping("findByStatus")
    @ApiOperation(
        value = "通过status查找Pet",
        notes = "多个status通过逗号隔开",
        response = Pet.class,
        responseContainer = "List",
        authorizations = @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write_pets", description = ""),
            @AuthorizationScope(scope = "read_pets", description = "")
        }))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "非法status值")})
    public Result<List<Pet>> findPetsByStatus(
        @ApiParam(value = "Status",
                  required = true,
                  defaultValue = "available",
                  allowableValues = "available,pending,sold",
                  allowMultiple = true)
        @RequestParam String status) {
        return this.ok(this.petData.findPetsByStatus(status));
    }

    /**
     * Find pets by tags response entity
     *
     * @param tags tags
     * @return the response entity
     * @since 1.0.0
     */
    @GetMapping("findByTags")
    @ApiOperation(
        value = "通过tags查找Pet",
        notes = "目前只支持单个tags查询",
        response = Pet.class,
        responseContainer = "List",
        authorizations = @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write_pets", description = ""),
            @AuthorizationScope(scope = "read_pets", description = "")
        }))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "非法tag值")})
    @Deprecated
    public Result<List<Pet>> findPetsByTags(
        @ApiParam(value = "Tags",
                  required = true,
                  allowMultiple = true)
        @RequestParam String tags) {
        return this.ok(this.petData.findPetsByTags(tags));
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.2.4
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.15 22:16
     * @since 1.0.0
     */
    private static class PetRepository extends MapBackedRepository<Long, Pet> {
        /**
         * Find pets by status list
         *
         * @param status status
         * @return the list
         * @since 1.0.0
         */
        List<Pet> findPetsByStatus(String status) {
            return this.where(Pets.statusIs(status));
        }

        /**
         * Find pets by tags list
         *
         * @param tags tags
         * @return the list
         * @since 1.0.0
         */
        List<Pet> findPetsByTags(String tags) {
            return this.where(Pets.tagsContain(tags));
        }
    }

}
