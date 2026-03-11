package com.libraryflow.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import com.libraryflow.common.DrivenAdapter;
import com.libraryflow.common.DrivingAdapter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.libraryflow",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureTest {

    // --- Package-basierte Regeln ---

    @ArchTest
    static final ArchRule app_should_not_access_adapters =
            noClasses().that().resideInAPackage("..app..")
                    .should().accessClassesThat().resideInAPackage("..adapter..")
                    .because("der Anwendungskern darf nicht auf Adapter zugreifen (Hexagonale Architektur)");

    @ArchTest
    static final ArchRule driving_adapters_should_not_access_driven_adapters =
            noClasses().that().resideInAPackage("..adapter.driving..")
                    .should().accessClassesThat().resideInAPackage("..adapter.driven..")
                    .because("Driving-Adapter dürfen nicht auf Driven-Adapter zugreifen (Adapter-Isolation)");

    @ArchTest
    static final ArchRule driven_adapters_should_not_access_driving_adapters =
            noClasses().that().resideInAPackage("..adapter.driven..")
                    .should().accessClassesThat().resideInAPackage("..adapter.driving..")
                    .because("Driven-Adapter dürfen nicht auf Driving-Adapter zugreifen (Adapter-Isolation)");

    @ArchTest
    static final ArchRule driven_adapter_slices_should_be_independent =
            SlicesRuleDefinition.slices().matching("..adapter.driven.(*)..")
                    .should().notDependOnEachOther()
                    .because("Driven-Adapter-Slices müssen unabhängig voneinander sein (kein Cross-Adapter-Coupling)");

    @ArchTest
    static final ArchRule common_should_not_access_app_or_adapter =
            noClasses().that().resideInAPackage("..common..")
                    .should().accessClassesThat().resideInAnyPackage("..app..", "..adapter..")
                    .because("das Common-Package (Annotationen) muss eigenständig bleiben");

    @ArchTest
    static final ArchRule no_package_cycles =
            SlicesRuleDefinition.slices().matching("com.libraryflow.(*)..")
                    .should().beFreeOfCycles()
                    .because("zyklische Package-Abhängigkeiten sind nicht erlaubt");

    // --- Annotation-basierte Regeln (duale Absicherung) ---

    @ArchTest
    static final ArchRule driving_adapter_classes_should_not_access_driven_adapter_classes =
            noClasses().that().areAnnotatedWith(DrivingAdapter.class)
                    .should().accessClassesThat().areAnnotatedWith(DrivenAdapter.class)
                    .because("@DrivingAdapter-Klassen dürfen nicht auf @DrivenAdapter-Klassen zugreifen");

    @ArchTest
    static final ArchRule driven_adapter_classes_should_not_access_driving_adapter_classes =
            noClasses().that().areAnnotatedWith(DrivenAdapter.class)
                    .should().accessClassesThat().areAnnotatedWith(DrivingAdapter.class)
                    .because("@DrivenAdapter-Klassen dürfen nicht auf @DrivingAdapter-Klassen zugreifen");
}
