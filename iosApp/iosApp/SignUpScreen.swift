
import SwiftUI
import Shared
import KMPNativeCoroutinesAsync

struct SignUpScreen : View{
    @ObservedObject var viewModel : ViewModel
    @State private var name:String  = ""
    @State private var email:String  = ""

    init(viewModel:ViewModel){
        self.viewModel =  viewModel
    }

    var body: some View{
        VStack {
            Text("Create New User")

            TextField( "Email", text: $email).textFieldStyle(.roundedBorder).padding().keyboardType(.emailAddress).autocapitalization(.none)

            TextField("Name" , text: $name).textFieldStyle(.roundedBorder).padding()

            Button("Sign Up"){
                viewModel.signUp(name: name, email:email)
            }

            if let createdUser = viewModel.createdUser{
                Text("User Created: \(createdUser.name)")
            }else if let errorMessage = viewModel.errorMessage{
                Text("Error: \(errorMessage)").foregroundColor(.red)
            }
            Spacer()
        }.padding().navigationTitle("Sign Up")
    }
}

extension SignUpScreen {
    @MainActor
    class ViewModel : ObservableObject{
        @Published var createdUser: User?=nil
        @Published var errorMessage: String? = nil

        private let greeting =  Greeting()

        func signUp(name: String, email: String ){
            errorMessage = nil
            Task{
                do{
                    let user =  try await asyncFunction(for: greeting.createUser(name:name, email: email))
                    self.createdUser = user
                }catch{
                    self.errorMessage =  "Failed to create user: \(error.localizedDescription)"
                    print(errorMessage!)
                }
            }
        }
    }
}
