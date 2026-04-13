package gov.Lab6;

import gov.Lab6.util.MovieDataImporter;

public class MovieDataImportMain {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: MovieDataImportMain <path/to/movies_metadata.csv> <path/to/credits.csv>");
            System.exit(1);
        }
        new MovieDataImporter().importFrom(args[0], args[1]);
    }
}
