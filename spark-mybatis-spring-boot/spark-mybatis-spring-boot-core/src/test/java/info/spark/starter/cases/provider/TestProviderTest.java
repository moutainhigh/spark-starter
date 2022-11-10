package info.spark.starter.cases.provider;

import info.spark.starter.entity.dto.TestDTO;
import info.spark.starter.entity.form.TestQuery;
import info.spark.starter.provider.TestProvider;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import lombok.SneakyThrows;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 16:58
 * @since 1.8.0
 */
@SparkTest
public class TestProviderTest {

    /** Test provider */
    @Resource
    private TestProvider testProvider;

    /**
     * Test 1
     *
     * @since 1.8.0
     */
    @SneakyThrows
    @Test
    void test_1() {
        this.testProvider.create(TestDTO.builder().build());
        this.testProvider.find(1L);
        this.testProvider.delete(1L);
        List<Long> longs = new ArrayList<Long>() {
            private static final long serialVersionUID = 429666079236455736L;

            {
                this.add(1L);
            }
        };

        this.testProvider.delete(longs);
        this.testProvider.update(TestDTO.builder().build());
        this.testProvider.find();
        this.testProvider.page(TestQuery.builder().build());

        this.testProvider.counts();

    }
}
