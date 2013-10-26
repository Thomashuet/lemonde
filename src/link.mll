{
  open Lexing
}

rule main = parse
| "<page>" { page "" lexbuf }
| _  { main lexbuf }

and page p = parse
| "</page>" { () }
| "<title>" { page (pagename lexbuf) lexbuf }
| "<redirect title=\"" { skip_page lexbuf }
| "<text xml:space=\"preserve\">" { wikitext p lexbuf; page p lexbuf }
| "<ns>" { if num lexbuf = 0 then page p lexbuf else skip_page lexbuf }
| _ { page p lexbuf }

and pagename = parse
| [^'<' '"' '|' ']']* as t { t }

and num = parse
| ['0'-'9']+ as n { int_of_string n }

and skip_page = parse
| "</page>" { () }
| _ { skip_page lexbuf }

and wikitext p = parse
| "<" { () }
| "&lt;nowiki&gt;" { nowiki lexbuf; wikitext p lexbuf }
| "[[" { let t = pagename lexbuf in
         if String.contains t ':' || String.contains t '|' || String.contains t '\n' then ()
         else anchor p t lexbuf; wikitext p lexbuf }
| _ { wikitext p lexbuf }

and anchor f t = parse
| "|"? (([^']' '|' '\n']|']'[^']' '|' '\n'])* as a) "]]" (['a'-'z' 'A'-'Z' '\128'-'\255']* as b)
  { print_endline (f ^ "\t" ^ (if a = "" then t else a) ^ b ^ "\t" ^ t) }
| _ { () }

and nowiki = parse
| "&lt;/nowiki&gt;" { () }
| _ { nowiki lexbuf }

{
  let lexbuf = from_channel stdin

  let () = while true do main lexbuf done
}
