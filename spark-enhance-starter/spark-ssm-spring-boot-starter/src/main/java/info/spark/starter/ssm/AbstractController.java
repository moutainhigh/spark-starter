package info.spark.starter.ssm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import info.spark.starter.basic.Result;
import info.spark.starter.common.base.AbstractBaseEntity;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseForm;
import info.spark.starter.common.base.BaseQuery;
import info.spark.starter.common.base.Bridge;
import info.spark.starter.common.base.IBaseEntity;
import info.spark.starter.common.base.ICrudDelegate;
import info.spark.starter.common.mapstruct.ViewConverter;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.R;
import info.spark.starter.rest.ServletController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>Description:  </p>
 *
 * @param <S> 底层操作类
 * @param <C> parameter
 * @param <F> parameter
 * @param <Q> parameter
 * @param <D> 出参
 * @param <V> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.15 11:16
 * @since 2.1.0
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class AbstractController<S extends ICrudDelegate<D>,
    C extends ViewConverter<F, D, V>,
    F extends BaseForm<? extends Serializable>,
    Q extends BaseQuery<? extends Serializable>,
    D extends BaseDTO<? extends Serializable>,
    V extends AbstractBaseEntity<? extends Serializable>> extends ServletController {

    /** Service */
    @Autowired
    protected S service;

    /** Conver */
    @Autowired
    protected C conver;

    /** Bridge */
    private Bridge<D> bridge;

    /**
     * Init
     *
     * @since 2.1.0
     */
    @PostConstruct
    public void init() {
        bridge = new Bridge<D>(service) {
        };
    }

    /**
     * Create
     *
     * @param form form
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<?> create(@RequestBody @NotNull F form) {
        D dto = this.conver.dto(form);
        return R.values(IBaseEntity.ID, bridge.create(dto, BaseCodes.OPTION_FAILURE));
    }

    /**
     * Create batch
     *
     * @param forms forms
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/batch",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createBatch(@RequestBody @NotNull Collection<F> forms) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createBatch(dtos, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create batch
     *
     * @param forms     forms
     * @param batchSize batch size
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/batch/{batchSize}",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createBatch(@RequestBody Collection<F> forms, @PathVariable("batchSize") int batchSize) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createBatch(dtos, batchSize, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create ignore
     *
     * @param form form
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/ignore",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createIgnore(@RequestBody @NotNull F form) {
        D dto = this.conver.dto(form);
        bridge.createIgnore(dto, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create ignore batch
     *
     * @param forms forms
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/ignore/batch",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createIgnoreBatch(@RequestBody @NotNull Collection<F> forms) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createIgnoreBatch(dtos, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create ignore batch
     *
     * @param forms     forms
     * @param batchSize batch size
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/ignore/batch/{batchSize}",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createIgnoreBatch(@RequestBody Collection<F> forms, @PathVariable("batchSize") int batchSize) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createIgnoreBatch(dtos, batchSize, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create replace
     *
     * @param form form
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/replace",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createReplace(@RequestBody @NotNull F form) {
        D dto = this.conver.dto(form);
        bridge.createReplace(dto, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create replace
     *
     * @param forms forms
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/replace/batch",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createReplace(@RequestBody @NotNull Collection<F> forms) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createReplaceBatch(dtos, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create replace
     *
     * @param forms     forms
     * @param batchSize batch size
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PostMapping(value = "/replace/batch/{batchSize}",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createReplace(@RequestBody Collection<F> forms, @PathVariable("batchSize") int batchSize) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createReplaceBatch(dtos, batchSize, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create or update
     *
     * @param form form
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<?> createOrUpdate(@RequestBody @NotNull F form) {
        D dto = this.conver.dto(form);
        return R.values(IBaseEntity.ID, bridge.createOrUpdate(dto, BaseCodes.OPTION_FAILURE));
    }

    /**
     * Create or update batch
     *
     * @param forms forms
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PatchMapping(value = "/batch",
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createOrUpdateBatch(@RequestBody @NotNull Collection<F> forms) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createOrUpdateBatch(dtos, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Create or update batch
     *
     * @param forms     forms
     * @param batchSize batch size
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PatchMapping(value = "/batch/{batchSize}",
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> createOrUpdateBatch(@RequestBody Collection<F> forms, @PathVariable("batchSize") int batchSize) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.createOrUpdateBatch(dtos, batchSize, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Update
     *
     * @param form form
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> update(@RequestBody @NotNull F form) {
        D dto = this.conver.dto(form);
        bridge.update(dto, BaseCodes.OPTION_FAILURE);
        return this.ok();
    }

    /**
     * Update batch
     *
     * @param forms forms
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PutMapping(value = "/batch",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> updateBatch(@RequestBody @NotNull Collection<F> forms) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.updateBatch(dtos, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Update batch
     *
     * @param forms     forms
     * @param batchSize batch size
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @PutMapping(value = "/batch/{batchSize}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> updateBatch(@RequestBody Collection<F> forms, @PathVariable("batchSize") int batchSize) {
        Collection<D> dtos = this.conver.dto(forms);
        bridge.updateBatch(dtos, batchSize, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Delete
     *
     * @param id id
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @DeleteMapping(value = "/{id}",
                   produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> delete(@PathVariable("id") @NotNull Serializable id) {
        bridge.delete(id, BaseCodes.OPTION_FAILURE);
        return this.ok();
    }

    /**
     * Delete batch
     *
     * @param ids ids
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @DeleteMapping(value = "/batch",
                   consumes = MediaType.APPLICATION_JSON_VALUE,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> deleteBatch(@RequestBody @NotEmpty List<? extends Serializable> ids) {
        bridge.delete(ids, BaseCodes.OPTION_FAILURE);
        return this.ok();
    }

    /**
     * Delete
     *
     * @param columnMap column map
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @DeleteMapping(value = "/batch/m",
                   consumes = MediaType.APPLICATION_JSON_VALUE,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> delete(@RequestBody Map<String, Object> columnMap) {
        bridge.delete(columnMap, BaseCodes.OPTION_FAILURE);
        return ok();
    }

    /**
     * Counts
     *
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(value = "/counts",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Integer> counts() {
        return ok(bridge.counts(BaseCodes.OPTION_FAILURE));
    }

    /**
     * Counts
     *
     * @param query query
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(value = "/counts/q",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Integer> counts(@NotNull Q query) {
        return ok(bridge.counts(query, BaseCodes.OPTION_FAILURE));
    }

    /**
     * Page
     *
     * @param query query
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(value = "page",
                consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<IPage<V>> page(@NotNull Q query) {
        return this.ok(this.conver.vo(bridge.page(query, BaseCodes.OPTION_FAILURE)));
    }

    /**
     * List
     *
     * @param query query
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<V>> list(@NotNull Q query) {
        return this.ok(this.conver.vo(bridge.list(query, BaseCodes.OPTION_FAILURE)));
    }

    /**
     * Find
     *
     * @param id id
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(value = "/{id}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<V> find(@PathVariable("id") @NotNull Serializable id) {
        return this.ok(this.conver.vo(bridge.find(id, BaseCodes.OPTION_FAILURE)));
    }

    /**
     * Find
     *
     * @param query query
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(value = "/one",
                consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<V> find(@NotNull Q query) {
        return this.ok(this.conver.vo(bridge.find(query, BaseCodes.OPTION_FAILURE)));
    }

    /**
     * Find
     *
     * @param ids ids
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(value = "/batch",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<V>> find(@RequestBody @NotEmpty List<? extends Serializable> ids) {
        return this.ok(this.conver.vo(bridge.find(ids, BaseCodes.OPTION_FAILURE)));
    }

    /**
     * Find
     *
     * @return the result
     * @since 2.1.0
     */
    @ResponseBody
    @GetMapping(value = "all",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<V>> find() {
        return this.ok(this.conver.vo(bridge.find(BaseCodes.OPTION_FAILURE)));
    }

}
