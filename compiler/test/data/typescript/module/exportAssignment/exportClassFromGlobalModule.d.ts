// based on atom.d.ts
declare class SampleView extends _atom.ScrollView {
    foo: string
}

declare namespace _atom {
    class ScrollView {

    }
}

export = SampleView;
