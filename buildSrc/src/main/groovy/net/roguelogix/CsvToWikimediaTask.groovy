package net.roguelogix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class CsvToWikimediaTask extends DefaultTask {
    @InputFile
    File csvInput

    @OutputFile
    File txtOutput

    @TaskAction
    void process() {
        def rows = csvInput.readLines().collect {it.replace(",", " || ")}
        rows.subList(1, rows.size()).sort()

        txtOutput.withWriter {
            it.writeLine('{| class="wikitable sortable"')
            rows.eachWithIndex { row, index ->
                if (index == 0) {
                    it.writeLine('! ' + row)
                } else {
                    it.writeLine('| ' + row)
                }
                it.writeLine('|-')
            }
            it.writeLine("|}")
        }
    }
}
