import com.xcs.spring.service.MyService
import org.springframework.beans.factory.InitializingBean

class MyServiceImpl implements MyService, InitializingBean {
    private String message

    void setMessage(String message) {
        this.message = message
    }

    String getMessage() {
        return message
    }

    @Override
    void afterPropertiesSet() throws Exception {
        System.out.println("MyServiceImpl.afterPropertiesSet")
    }

    @Override
    void showMessage() {
        System.out.println(message)
    }
}

beans {
    myServiceImpl(MyServiceImpl) {
        message = "hello world123456"
    }
}