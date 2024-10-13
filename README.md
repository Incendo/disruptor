<div align="center">
<h1>disruptor</h1>

![license](https://img.shields.io/github/license/incendo/disruptor.svg)
[![central](https://img.shields.io/maven-central/v/org.incendo/disruptor-core)](https://search.maven.org/search?q=org.incendo)
![build](https://img.shields.io/github/actions/workflow/status/incendo/disruptor/build.yml?logo=github)
</div>

library for introducing disruptions to your code to replicate an unstable live environment.

## Modules

- **core:** core disruptor API
- **spring:** spring integration
- **openfeign:** feign integration

## Links

- [JavaDoc (core)](https://javadoc.io/doc/org.incendo/disruptor-core/latest/index.html)
- [JavaDoc (openfeign)](https://javadoc.io/doc/org.incendo/disruptor-openfeign/latest/index.html)
- [JavaDoc (spring)](https://javadoc.io/doc/org.incendo/disruptor-spring/latest/index.html)

## Usage

### Installation

**Maven**:

```xml
<dependency>
    <groupId>org.incendo</groupId>
    <!-- disruptor-core, disruptor-spring, disruptor-openfeign -->
    <artifactId>disruptor-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle (Kotlin)**

```kotlin
implementation("org.incendo:disruptor-core:1.0.0")
```

**Gradle (Groovy)**

```groovy
implementation 'org.incendo:disruptor-core:1.0.0'
```

### Pure Java

```java
final Disruptor disruptor = Disruptor.builder()
        .group(
                // Creates a new group called "test"...
                "test",
                group -> group
                        .config(
                                // which triggers 25% of the time, and lasts for 5 seconds once triggered
                                DisruptionTrigger.random(0.25f).lasting(Duration.ofSeconds(5L)),
                                config -> config
                                        // running before the method that is disrupted
                                        .mode(DisruptionMode.BEFORE)
                                        // introducing a delay of 5 seconds
                                        .delay(Duration.ofSeconds(5L))
                                        // then throwing an exception
                                        .throwException(ctx -> new RuntimeException("hello :)"))
                        )
        )
        .build();

disruptor.disruptWithoutResult("test", () -> {
    System.out.println("test");
});
```

### OpenFeign

```java
final DisruptorCapability capability = DisruptorCapability.of(
        disruptor,
        "group"
);
Feign.builder()/*...*/.addCapability(capability)/*...*/;

// Or, if using spring-cloud-openfeign:
@Bean
Capability disruptorCapability() {
    return DisruptorCapability(disruptor, "group");
}
```

### Spring

```java
import java.beans.BeanProperty;

@Configuration
public class YourConfig {

    @Bean
    Disruptor disruptor() {
        return Disruptor.builder()/*...*/.build();
    }
}

@Disrupt("group") // You may annotate a class...
public class YourService {
    
    @Disrupt("other-group") //... or a method
    public void yourMethod() {
    }
}
```