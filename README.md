# Konfiguracja środowiska

Przed budową projektu, upewnij się, że masz ustawione dwie zmienne środowiskowe:

- `PYTHON_PATH`: Ścieżka do instalacji Pythona na twoim komputerze.
- `PYTHON_VERSION`: Wersja Pythona, której używasz.

## Ustawianie zmiennych środowiskowych

### Windows

1. Otwórz zmienne środowiskowe
2. W sekcji Zmienne systemowe, kliknij Nowy.
3. Wpisz `PYTHON_PATH` jako nazwę zmiennej i ścieżkę do instalacji Pythona jako wartość zmiennej.
4. Powtórz kroki 2-3 dla zmiennej `PYTHON_VERSION`, wpisując odpowiednią wersję Pythona jako wartość.
5. Kliknij OK, aby zamknąć wszystkie okna dialogowe.
6. Jeśli Gradle nie wykrywa zmian, zrestartuj Android Studio.

### Unix (Linux/Mac)

1. Otwórz terminal.
2. Wpisz `export PYTHON_PATH=/ścieżka/do/twojego/pythona`.
3. Wpisz `export PYTHON_VERSION=wersja_pythona`.
4. Aby zmiany były trwałe, dodaj powyższe linie do pliku inicjalizacji powłoki (np. `~/.bashrc` lub `~/.zshrc`).
