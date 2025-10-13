import SwiftUI

@main
struct iOSApp: App {
    @StateObject private var router = NavigationRouter()

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $router.path) {
                SignUpScreen(viewModel: .init())
                    .navigationDestination(for: NavigationRoute.self) { route in
                        switch route {
                        case .home:
                            HomeScreen()
                        }
                    }
            }
            .environmentObject(router)
        }
    }
}

// MARK: - Navigation
enum NavigationRoute: Hashable {
    case home
}

@MainActor
class NavigationRouter: ObservableObject {
    @Published var path = [NavigationRoute]()

    func navigateTo(_ route: NavigationRoute) {
        path.append(route)
    }

    func popToRoot() {
        path.removeAll()
    }
}