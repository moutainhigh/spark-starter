package info.spark.agent.adapter.client;

import com.fasterxml.jackson.core.type.TypeReference;
import info.spark.agent.adapter.enums.AgentClientErrorCodes;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.StandardResult;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.util.JsonUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.13 20:18
 * @since 1.6.0
 */
public class DefaultAgentResult implements AgentResult {
    /** Result */
    private final Result<?> result;

    /**
     * Default agent result
     *
     * @param result result
     * @since 1.6.0
     */
    @Contract(pure = true)
    public DefaultAgentResult(Result<?> result) {
        this.result = result;
    }

    /**
     * And return
     *
     * @param <T>   parameter
     * @param clazz clazz
     * @return the t
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> AgentOptional<T> expect(Class<T> clazz) {
        // 如果业务端没有调用 failException, 需要判断请求是否成功, 不成功则返回 null
        if (this.result.isFail() || null == this.result.getData()) {
            return AgentOptional.empty();
        }

        Object data = this.result.getData();
        if ((data instanceof LinkedHashMap) && CollectionUtils.isEmpty((LinkedHashMap<String, String>) data)) {
            return AgentOptional.empty();
        } else if (data instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) data;
            return AgentOptional.of((T) charSequence.toString());
        } else {
            // 重新使用 responseType 反序列化 data, 避免类型转换异常
            data = JsonUtils.parse(JsonUtils.toJson(data), clazz);
        }

        return AgentOptional.ofNullable((T) data);
    }

    /**
     * And return
     *
     * @param <T>          parameter
     * @param responseType 不能使用 Result 包装
     * @return the t
     * @since 1.6.0
     */
    @Override
    public <T> AgentOptional<T> expect(@NotNull TypeReference<T> responseType) {
        // 判断 responseType 是否为 new TypeReference<Result<T>() {}, 不支持这样包装方式
        ParameterizedType parameterizedType = (ParameterizedType) responseType.getType();
        Assertions.isFalse(Result.class.equals(parameterizedType.getRawType()), AgentClientErrorCodes.RESULT_TYPE_ERRORR.getMessage());

        // 如果业务端没有调用 failException, 需要判断请求是否成功, 不成功则返回 null
        if (this.result.isFail() || null == this.result.getData()) {
            return AgentOptional.empty();
        }

        return AgentOptional.of(JsonUtils.parse(JsonUtils.toJson(this.result.getData()), responseType));
    }

    /**
     * agent service 无返回值使用, 如果 data 有数据, 则是 LinkedHashMap 类型, 不要使用强制转换以免造成类型转换异常.
     *
     * @return the result
     * @since 1.6.0
     */
    @Override
    public Result<?> expect() {
        return this.result.getData() == null
               ? StandardResult.build(this.result.getCode(), this.result.getMessage())
               : this.result;
    }

}
