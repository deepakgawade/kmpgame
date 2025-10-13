

import SwiftUI

struct HomeScreen: View {
    var body: some View{
        VStack{
            Text("Welcome!").font(.largeTitle)
            Text("You are logged in")
        }.navigationTitle("Home")
    }
}

struct HomeScreen_Previews: PreviewProvider{
    static var previews: some View{
        HomeScreen()
    }
}
