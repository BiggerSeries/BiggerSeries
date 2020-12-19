package net.roguelogix

import blue.endless.jankson.Jankson
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class Json5ToCsvTask extends DefaultTask {
    @InputFiles
    FileCollection jsonInputs

    @OutputFile
    File csvOutput

    @Input
    List<String> records

    @TaskAction
    void process() {
        csvOutput.withWriter {
            def printer = new CSVPrinter(it, CSVFormat.DEFAULT)
            printer.printRecord(this.records)
            jsonInputs.each {
                def parser = Jankson.builder().build().load(it)
                def row = this.records.collect { parser.get(String.class, it) }
                printer.printRecord(row)
            }
        }
    }
}
