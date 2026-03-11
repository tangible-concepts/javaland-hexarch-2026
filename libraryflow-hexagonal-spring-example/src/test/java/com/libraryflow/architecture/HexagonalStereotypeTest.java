package com.libraryflow.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import com.libraryflow.common.DrivenAdapter;
import com.libraryflow.common.DrivenPort;
import com.libraryflow.common.DrivingAdapter;
import com.libraryflow.common.DrivingPort;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "com.libraryflow",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStereotypeTest {

    // --- Ports müssen Interfaces sein ---

    @ArchTest
    static final ArchRule driving_ports_must_be_interfaces =
            classes().that().areAnnotatedWith(DrivingPort.class)
                    .should().beInterfaces()
                    .because("@DrivingPort definiert einen Vertrag und muss ein Interface sein");

    @ArchTest
    static final ArchRule driven_ports_must_be_interfaces =
            classes().that().areAnnotatedWith(DrivenPort.class)
                    .should().beInterfaces()
                    .because("@DrivenPort definiert einen Vertrag und muss ein Interface sein");

    // --- Ports müssen im app-Package liegen ---

    @ArchTest
    static final ArchRule driving_ports_must_reside_in_app =
            classes().that().areAnnotatedWith(DrivingPort.class)
                    .should().resideInAPackage("..app..")
                    .because("@DrivingPort gehört zum Anwendungskern (app-Package)");

    @ArchTest
    static final ArchRule driven_ports_must_reside_in_app =
            classes().that().areAnnotatedWith(DrivenPort.class)
                    .should().resideInAPackage("..app..")
                    .because("@DrivenPort gehört zum Anwendungskern (app-Package)");

    // --- Adapter müssen im richtigen Package liegen ---

    @ArchTest
    static final ArchRule driving_adapters_must_reside_in_adapter_driving =
            classes().that().areAnnotatedWith(DrivingAdapter.class)
                    .should().resideInAPackage("..adapter.driving..")
                    .because("@DrivingAdapter muss im adapter.driving-Package liegen");

    @ArchTest
    static final ArchRule driven_adapters_must_reside_in_adapter_driven =
            classes().that().areAnnotatedWith(DrivenAdapter.class)
                    .should().resideInAPackage("..adapter.driven..")
                    .because("@DrivenAdapter muss im adapter.driven-Package liegen");

    // --- Driven Adapter müssen ein Driven Port implementieren ---

    @ArchTest
    static final ArchRule driven_adapters_must_implement_driven_port =
            classes().that().areAnnotatedWith(DrivenAdapter.class)
                    .should(implementAtLeastOneDrivenPort())
                    .because("@DrivenAdapter muss mindestens ein @DrivenPort-Interface implementieren");

    // --- Adapter dürfen keine Interfaces sein ---

    @ArchTest
    static final ArchRule driving_adapters_must_not_be_interfaces =
            classes().that().areAnnotatedWith(DrivingAdapter.class)
                    .should().notBeInterfaces()
                    .because("@DrivingAdapter muss eine konkrete Klasse sein, kein Interface");

    @ArchTest
    static final ArchRule driven_adapters_must_not_be_interfaces =
            classes().that().areAnnotatedWith(DrivenAdapter.class)
                    .should().notBeInterfaces()
                    .because("@DrivenAdapter muss eine konkrete Klasse sein, kein Interface");

    // --- Custom ArchCondition ---

    private static ArchCondition<JavaClass> implementAtLeastOneDrivenPort() {
        return new ArchCondition<>("mindestens ein @DrivenPort-Interface implementieren") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean implementsDrivenPort = javaClass.getAllRawInterfaces().stream()
                        .anyMatch(i -> i.isAnnotatedWith(DrivenPort.class));
                if (!implementsDrivenPort) {
                    events.add(SimpleConditionEvent.violated(javaClass,
                            String.format("%s ist mit @DrivenAdapter annotiert, implementiert aber kein @DrivenPort-Interface",
                                    javaClass.getFullName())));
                }
            }
        };
    }
}
