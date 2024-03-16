package common;

import java.io.Serializable;

/**
 * RPC 响应封装
 */
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private Throwable error;
    private Object result;

    // 判断RPC调用是否成功
    public boolean isSuccess() {
        return this.error == null;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
