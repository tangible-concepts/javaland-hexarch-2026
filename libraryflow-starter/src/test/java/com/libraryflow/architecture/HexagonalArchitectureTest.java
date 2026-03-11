package com.libraryflow.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.libraryflow",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule app_should_not_access_adapters =
            noClasses().that().resideInAPackage("..app..")
                    .should().accessClassesThat().resideInAPackage("..adapter..")
                    .because("der Anwendungskern darf nicht auf Adapter zugreifen (Hexagonale Architektur)")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule driving_adapters_should_not_access_driven_adapters =
            noClasses().that().resideInAPackage("..adapter.driving..")
                    .should().accessClassesThat().resideInAPackage("..adapter.driven..")
                    .because("Driving-Adapter dürfen nicht auf Driven-Adapter zugreifen (Adapter-Isolation)")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule driven_adapters_should_not_access_driving_adapters =
            noClasses().that().resideInAPackage("..adapter.driven..")
                    .should().accessClassesThat().resideInAPackage("..adapter.driving..")
                    .because("Driven-Adapter dürfen nicht auf Driving-Adapter zugreifen (Adapter-Isolation)")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule driven_adapter_slices_should_be_independent =
            SlicesRuleDefinition.slices().matching("..adapter.driven.(*)..")
                    .should().notDependOnEachOther()
                    .because("Driven-Adapter-Slices müssen unabhängig voneinander sein (kein Cross-Adapter-Coupling)")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule no_package_cycles =
            SlicesRuleDefinition.slices().matching("com.libraryflow.(*)..")
                    .should().beFreeOfCycles()
                    .because("zyklische Package-Abhängigkeiten sind nicht erlaubt")
                    .allowEmptyShould(true);
}
