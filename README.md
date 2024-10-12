<div align="center">
<h1>disruptor</h1>

![license](https://img.shields.io/github/license/incendo/disruptor.svg)
![build](https://img.shields.io/github/actions/workflow/status/incendo/disruptor/build.yml?logo=github)
</div>

library for introducing disruptions to your code to replicate an unstable live environment.

## Modules

- **core:** core disruptor API
- ~~**spring:** spring integration~~
- ~~**feign:** feign integration~~

## Example

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