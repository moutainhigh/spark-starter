package info.spark.starter.ip2region;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.nutz.plugins.ip2region.DataBlock;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.05 16:20
 * @since 1.7.0
 */
public final class DataBlockWrapper {

    /** Block */
    private final DataBlock block;

    /** Region addr */
    @Getter
    @Setter
    private RegionAddress regionAddr;

    /**
     * Wrapper
     *
     * @param block block
     * @return the data block wrapper
     * @since 1.7.0
     */
    @Contract("_ -> new")
    public static @NotNull DataBlockWrapper wrapper(DataBlock block) {
        return new DataBlockWrapper(block);
    }

    /**
     * Data block wrapper
     *
     * @param block block
     * @since 1.7.0
     */
    private DataBlockWrapper(DataBlock block) {
        this.block = block;
        this.regionAddr = new RegionAddress(block.getRegion().split("\\|"));
    }

    /**
     * Gets city id *
     *
     * @return the city id
     * @since 1.7.0
     */
    public int getCityId() {
        return this.block.getCityId();
    }

    /**
     * Sets city id *
     *
     * @param cityId city id
     * @return the city id
     * @since 1.7.0
     */
    public DataBlock setCityId(int cityId) {
        this.block.setCityId(cityId);
        return this.block;
    }

    /**
     * Gets region *
     *
     * @return the region
     * @since 1.7.0
     */
    public String getRegion() {
        return this.block.getRegion();
    }

    /**
     * Sets region *
     *
     * @param region region
     * @return the region
     * @since 1.7.0
     */
    public DataBlock setRegion(String region) {
        this.block.setRegion(region);
        return this.block;
    }

    /**
     * Gets data ptr *
     *
     * @return the data ptr
     * @since 1.7.0
     */
    public int getDataPtr() {
        return this.block.getDataPtr();
    }

    /**
     * Sets data ptr *
     *
     * @param dataPtr data ptr
     * @return the data ptr
     * @since 1.7.0
     */
    public DataBlock setDataPtr(int dataPtr) {
        this.block.setDataPtr(dataPtr);
        return this.block;
    }

    /**
     * To string
     *
     * @return the string
     * @since 1.7.0
     */
    @Override
    public String toString() {
        return String.valueOf(this.block.getCityId()) + '|' + this.block.getRegion() + '|' + this.block.getDataPtr();
    }

}
