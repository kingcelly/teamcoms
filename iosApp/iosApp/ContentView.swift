import SwiftUI
import ComposeApp

// Bridges the shared Compose Multiplatform UI (MainViewController(), defined
// in composeApp/src/iosMain) into a SwiftUI view the app can display.
struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard) // Compose handles the keyboard itself
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
